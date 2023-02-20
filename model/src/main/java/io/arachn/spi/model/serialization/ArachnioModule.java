/*-
 * =================================LICENSE_START==================================
 * model
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2023 aleph0
 * ====================================SECTION=====================================
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
 * ==================================LICENSE_END===================================
 */
package io.arachn.spi.model.serialization;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.arachn.spi.model.EntityMetadata;
import io.arachn.spi.model.UnrecognizedEntityMetadata;
import io.arachn.spi.model.WebpageEntityMetadata;
import io.arachn.spi.model.serialization.deser.MultiLevelInheritanceDeserializer;

public class ArachnioModule extends SimpleModule {
  private static final long serialVersionUID = 1260684766179870205L;

  @SuppressWarnings({"unchecked", "rawtypes"})
  public ArachnioModule() {
    addDeserializer((Class) EntityMetadata.class, new MultiLevelInheritanceDeserializer());
    addDeserializer((Class) UnrecognizedEntityMetadata.class, new MultiLevelInheritanceDeserializer());
    addDeserializer((Class) WebpageEntityMetadata.class, new MultiLevelInheritanceDeserializer());
  }  
}
