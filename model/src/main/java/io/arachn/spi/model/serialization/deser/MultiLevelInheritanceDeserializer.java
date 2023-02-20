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

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

/**
 * Handles Jackson multi-level inheritance hierarchies, which are otherwise unsupported. To use this
 * serializer, users must register all 1st-level (so, child of root) classes with a 2nd-level child.
 * In practice, it may be simpler just to register the whole type hierarchy, which also works fine.
 * The only drawback is it incurs a (likely small) additional amount of buffering in-memory.
 *
 * @see <a href=
 *      "https://github.com/FasterXML/jackson-databind/issues/374">https://github.com/FasterXML/jackson-databind/issues/374</a>
 * @see <a href="https://github.com/pedroviniv/multi-discriminators-polymorphic-deserializer">
 *      https://github.com/pedroviniv/multi-discriminators-polymorphic-deserializer</a>
 */
public class MultiLevelInheritanceDeserializer extends JsonDeserializer<Object>
    implements ContextualDeserializer {

  private JavaType type;

  /**
   * just for assigning the annotated field type as member field of this instance
   */
  @Override
  public JsonDeserializer<?> createContextual(final DeserializationContext ctxt,
      final BeanProperty property) throws JsonMappingException {
    if (property != null) {
      this.type = property.getType();
    }
    return this;
  }

  /**
   * navigates through the given class hierarchy to find the concrete implementation based in the
   * provided discriminator properties defined in the class
   * 
   * @param current
   * @param fieldValueExtractor
   * @return
   */
  private Class<?> resolveConcreteType(final Class<?> current,
      Function<String, String> fieldValueExtractor) {

    // Gets, through reflection, the TypeInfo annotation from the class
    final JsonTypeInfo currentTypeInfo = current.getAnnotation(JsonTypeInfo.class);

    // If it isn't annotated, the given class is the leaf
    if (currentTypeInfo == null) {
      return current;
    }

    // gets the SubTypes annotation thich defines what is the specializations of the super type
    // mapped to the discriminator value
    JsonSubTypes subTypes = current.getAnnotation(JsonSubTypes.class);
    final Map<String, Class<?>> subTypesByDiscriminatorValue = Stream.of(subTypes.value())
        .collect(Collectors.toMap(JsonSubTypes.Type::name, JsonSubTypes.Type::value));

    // Gets the JSON property to use as the discriminator
    final String subTypeDiscriminatorProperty = currentTypeInfo.property();

    // Gets the discriminator value using the JSON property obtained above
    String discriminatorValue = fieldValueExtractor.apply(subTypeDiscriminatorProperty);

    // Finally, through the map created above, it gets the class mapped to the discriminator value.
    Class<?> subType = subTypesByDiscriminatorValue.get(discriminatorValue);

    // Recursively calls the method with the subType found and the JSON field value extractor.
    return resolveConcreteType(subType, fieldValueExtractor);
  }

  @Override
  public Object deserialize(final JsonParser jsonParser, final DeserializationContext context)
      throws IOException, JsonProcessingException {

    // Reads the entire JSON node being deserialized as a tree. This is a theoretically an unbounded
    // amount of data, but in practice is pretty safe. The implementation has to do this (or
    // something like it) to pick up multiple discriminator fields whose order in the JSON may not
    // match their useful order during deserialization.
    // TODO Can this be avoided with a TokenBuffer?
    final ObjectCodec oc = jsonParser.getCodec();
    final JsonNode node = oc.readTree(jsonParser);

    // Gets the type of the node being serialized
    final Class<?> deserializingType = this.type.getRawClass();

    // Resolve the concrete type based in the node being serialized and the super type of the field
    Class<?> concreteType = this.resolveConcreteType(deserializingType, (fieldName) -> {
      JsonNode jsonNode = node.get(fieldName);
      return jsonNode.asText();
    });

    // Traverse the node to get the node parser and finally, reads the node providing the concrete
    // class that must be created
    JsonParser parserOfNode = node.traverse(oc);
    Object concreteInstance = parserOfNode.readValueAs(concreteType);

    return concreteInstance;
  }
}
