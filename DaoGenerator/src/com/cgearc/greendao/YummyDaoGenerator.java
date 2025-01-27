/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
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
 */
package com.cgearc.greendao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Generates entities and DAOs for the example project DaoExample.
 * 
 * Run it as a Java application (not Android).
 * 
 * @author Markus
 */
public class YummyDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(2, "com.georgeme");
        schema.enableKeepSectionsByDefault();
        schema.enableActiveEntitiesByDefault();
        addArticleTable(schema);

        new DaoGenerator().generateAll(schema, "F:/DOZO/Android/workspace_ideas/GeorgeMe/src-gen");
    }

    private static void addArticleTable(Schema schema) {
        Entity restaurant = schema.addEntity("Restaurant");
        restaurant.implementsInterface("Serializable");
        restaurant.addIdProperty();
        restaurant.addStringProperty("description");
        restaurant.addStringProperty("address");
        restaurant.addStringProperty("source");
        restaurant.addStringProperty("time");
        restaurant.addStringProperty("name");
        restaurant.addStringProperty("latitude");
        restaurant.addStringProperty("longitude");
        restaurant.addStringProperty("comments");
        restaurant.addStringProperty("photos");
        restaurant.addStringProperty("price");
        restaurant.addStringProperty("rating");
        restaurant.addStringProperty("distance");
        restaurant.addStringProperty("cover");
        
        Entity user = schema.addEntity("User");
        user.addIdProperty();
        user.implementsInterface("Serializable");
        user.addStringProperty("name");
        user.addStringProperty("picture");
        user.addStringProperty("lantitude");
        user.addStringProperty("longitude");

        
       
    }
   

}
