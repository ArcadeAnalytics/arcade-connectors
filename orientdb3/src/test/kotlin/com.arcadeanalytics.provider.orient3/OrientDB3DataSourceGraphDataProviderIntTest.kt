package com.arcadeanalytics.provider.orient3

import com.arcadeanalytics.provider.orient3.OrientDB3Container.dataSource
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrientDB3DataSourceGraphDataProviderIntTest {


    private val provider: OrientDB3DataSourceGraphDataProvider


    init {

        provider = OrientDB3DataSourceGraphDataProvider()
    }


    @Test
    @Throws(Exception::class)
    fun shouldFetchDataWithQuery() {

        //given


        //when

        val query = "select from Person limit 20"
        val data = provider.fetchData(OrientDB3Container.dataSource, query, 20)


        //then
        assertThat(data.nodes).hasSize(4)

        //        assertThat(data.getEdges()).hasSize(1);

        assertThat(data.nodesClasses).containsKeys("Person")
        //        assertThat(data.getEdgesClasses()).containsKeys("FriendOf");

        val cytoData = data.nodes.first()
        assertThat(cytoData.group).isEqualTo("nodes")
        assertThat(cytoData.data.source).isEmpty()

        val record = cytoData.data.record
        assertThat(record).isNotNull
                .containsKeys("name", "@out", "@in", "@edgeCount")

    }


    @Test
    @Throws(Exception::class)
    fun shouldTraverseFromGivenNode() {
        //given


        //        final String rid = getFirstPersonIdentity();

        val ids = getPersonsIdentity(2)
        //when
        val data = provider.expand(dataSource, ids, "out", "FriendOf", 300)

        //then
        assertThat(data.nodes).hasSize(2)
        assertThat(data.edges).hasSize(1)

        assertThat(data.nodesClasses).containsKeys("Person")
        assertThat(data.edgesClasses).containsKeys("FriendOf")

        val cytoData = data.nodes.stream().findFirst().get()
        assertThat(cytoData.data.record).isNotNull
        assertThat(cytoData.data.source).isEmpty()

    }

    @Test
    @Throws(Exception::class)
    fun shouldTraverseAllEdgesNode() {
        //given


        //        final String rid = getFirstPersonIdentity();

        val ids = getPersonsIdentity(2)
        //when
        val data = provider.expand(dataSource, ids, "both", "", 300)

        println("data = ${data}")
        //then
        assertThat(data.nodes).hasSize(4)
        assertThat(data.edges).hasSize(3)

        assertThat(data.nodesClasses).containsKeys("Person")
        assertThat(data.edgesClasses).containsKeys("FriendOf")
        assertThat(data.edgesClasses).containsKeys("HaterOf")

        val cytoData = data.nodes.stream().findFirst().get()
        assertThat(cytoData.data.record).isNotNull
        assertThat(cytoData.data.source).isEmpty()

    }

    @Test
    @Throws(Exception::class)
    fun shouldLoadGivenIds() {
        //given
        val ids = getPersonsIdentity(2)
        //when

        println("ids = " + ids)
        val data = provider.load(dataSource, ids)

        //then
        assertThat(data.nodes).hasSize(2)


        val cytoData = data.nodes.stream().findFirst().get()
        assertThat(cytoData.data.record).isNotNull
        assertThat(cytoData.data.source).isEmpty()

    }

    @Test
    fun shouldLoadFromClass() {
        val data = provider.loadFromClass(dataSource, "Person", 1)

        assertThat(data.nodes).hasSize(1)


    }

    @Test
    fun shouldLoadFromClassWherePropertyHasValue() {
        val data = provider.loadFromClass(dataSource, "Person", "name", "frank", 10)

        assertThat(data.nodes).hasSize(1)


    }

    private fun getPersonsIdentity(count: Int): Array<String> {
        val orientDB = OrientDB(getServerUrl(OrientDB3Container.getContainer()), OrientDBConfig.defaultConfig())
        orientDB.open(dataSource.name, "admin", "admin")
                .use {

                    return it.execute("sql","SELECT from Person")
                            .asSequence()
                            .take(count)
                            .map { doc -> doc.identity }
                            .map { id -> id.get().clusterId.toString() + "_" + id.get().clusterPosition }
                            .toList()
                            .toTypedArray()

                }

    }

}