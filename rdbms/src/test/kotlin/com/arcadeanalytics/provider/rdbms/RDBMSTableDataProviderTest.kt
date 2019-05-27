package com.arcadeanalytics.provider.rdbms

import com.arcadeanalytics.provider.DataSourceInfo
import com.arcadeanalytics.provider.TABLE_CLASS
import com.arcadeanalytics.provider.TypeProperties
import com.arcadeanalytics.provider.rdbms.dataprovider.PostgreSQLContainerHolder
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer

internal class RDBMSTableDataProviderTest {

    private val container: PostgreSQLContainer<Nothing> = PostgreSQLContainerHolder.container as PostgreSQLContainer<Nothing>

    private lateinit var provider: RDBMSTableDataProvider

    private lateinit var dataSourceInfo: DataSourceInfo

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {


        dataSourceInfo = DataSourceInfo(id = 1L,
                type = "RDBMS_POSTGRESQL",
                name = "testDataSource",
                server = container.containerIpAddress,
                port = container.firstMappedPort,
                username = container.username,
                password = container.password,
                database = container.databaseName,
                aggregationEnabled = true
        )

        provider = RDBMSTableDataProvider()

    }

    @Test
    fun fetchData() {


        val data = provider.fetchData(dataSourceInfo, """SELECT customer_id, SUM (amount) total_amount
            | FROM  payment
            | GROUP BY customer_id
            | ORDER BY total_amount DESC
            | """.trimMargin(), 100)

        val tableClass = data.nodesClasses[TABLE_CLASS]

        Assertions.assertThat(tableClass).containsKeys("name", "cardinality", "properties")

        val properties: TypeProperties = tableClass?.get("properties") as TypeProperties

        Assertions.assertThat(properties).containsKeys("customer_id", "total_amount")


        val cytoData = data.nodes.first()
        Assertions.assertThat(cytoData.classes).isEqualTo(TABLE_CLASS)

        Assertions.assertThat(cytoData.group)
                .isEqualTo("nodes")

        Assertions.assertThat(cytoData.data.id).isEqualTo("0")
        Assertions.assertThat(cytoData.data.source).isEmpty()
        Assertions.assertThat(cytoData.data.target).isEmpty()

        val record = cytoData.data.record

        Assertions.assertThat(record).containsOnlyKeys("customer_id", "total_amount")

    }
}