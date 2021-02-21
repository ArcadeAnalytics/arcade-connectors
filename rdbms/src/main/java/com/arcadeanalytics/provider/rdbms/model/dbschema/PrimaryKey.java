package com.arcadeanalytics.provider.rdbms.model.dbschema;

/*-
 * #%L
 * Arcade Connectors
 * %%
 * Copyright (C) 2018 - 2021 ArcadeData
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;

/**
 * It represents a primary key for an entity.
 *
 * @author Gabriele Ponzi
 */

public class PrimaryKey extends Key {

    public PrimaryKey(Entity belongingEntity, List<Attribute> involvedAttributes) {
        super(belongingEntity, involvedAttributes);
    }

    public Attribute getAttributeByOrdinalPosition(int ordinalPosition) {
        // overflow
        if (ordinalPosition > super.getInvolvedAttributes().size()) return null;

        for (Attribute attribute : super.involvedAttributes) {
            if (attribute.getOrdinalPosition() == ordinalPosition) return attribute;
        }
        return null;
    }
}
