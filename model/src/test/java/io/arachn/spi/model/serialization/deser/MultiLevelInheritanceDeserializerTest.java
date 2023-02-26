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
package io.arachn.spi.model.serialization.deser;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import org.junit.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.arachn.spi.model.ExtractedLink;
import io.arachn.spi.model.OtherEntityMetadata;
import io.arachn.spi.model.serialization.ArachnioClientModule;

public class MultiLevelInheritanceDeserializerTest {
  public static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new ArachnioClientModule());

  @Test
  public void test() throws IOException {
    String json =
        "{\"link\":{\"original\":{\"link\":\"http://www.feedingamerica.org/penny\",\"scheme\":\"http\",\"authority\":{\"host\":{\"type\":\"domain\",\"domain\":{\"registrySuffix\":\"org\",\"publicSuffix\":\"feedingamerica.org\",\"hostname\":\"www.feedingamerica.org\"}},\"port\":null},\"path\":\"/penny\",\"queryParameters\":[]},\"unwound\":null,\"outcome\":\"networkingError\",\"canonical\":null},\"entity\":{\"entityType\":\"other\"}}";

    ExtractedLink link = MAPPER.readValue(json, ExtractedLink.class);

    assertThat(link.getEntity(), instanceOf(OtherEntityMetadata.class));
  }
}
