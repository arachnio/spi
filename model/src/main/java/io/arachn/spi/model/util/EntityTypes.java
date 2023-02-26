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
package io.arachn.spi.model.util;

import io.arachn.spi.model.OtherEntityMetadata;
import io.arachn.spi.model.WebpageEntityMetadata;

public final class EntityTypes {
  private EntityTypes() {}

  /**
   * @see WebpageEntityMetadata
   */
  public static final String WEBPAGE = "webpage";

  /**
   * @see OtherEntityMetadata
   */
  public static final String OTHER = "other";
}
