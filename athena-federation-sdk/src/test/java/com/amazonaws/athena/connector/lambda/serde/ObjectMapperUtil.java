package com.amazonaws.athena.connector.lambda.serde;

/*-
 * #%L
 * Amazon Athena Query Federation SDK
 * %%
 * Copyright (C) 2019 Amazon Web Services
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

import com.amazonaws.athena.connector.lambda.data.BlockAllocator;
import com.amazonaws.athena.connector.lambda.data.BlockAllocatorImpl;
import com.amazonaws.athena.connector.lambda.request.FederationRequest;
import com.amazonaws.athena.connector.lambda.request.FederationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.amazonaws.athena.connector.lambda.utils.TestUtils.SERDE_VERSION_THREE;
import static com.amazonaws.athena.connector.lambda.utils.TestUtils.SERDE_VERSION_TWO;
import static org.junit.Assert.assertEquals;

public class ObjectMapperUtil
{
    private ObjectMapperUtil() {}

    public static <T> void assertSerialization(Object object)
    {
        assertBaseSerialization(object);
        assertSerializationForwardsCompatibleV2toV3(object);
        assertSerializationBackwardsCompatibleV3toV2(object);
    }

    private static <T> void assertBaseSerialization(Object object)
    {
        Class<?> clazz = object.getClass();
        if (object instanceof FederationRequest)
            clazz = FederationRequest.class;
        else if (object instanceof FederationResponse) {
            clazz = FederationResponse.class;
        }
        try (BlockAllocator allocator = new BlockAllocatorImpl()){
            // check SerDe write, SerDe read
            ByteArrayOutputStream serDeOut = new ByteArrayOutputStream();
            ObjectMapper serDe = VersionedObjectMapperFactory.create(allocator, SERDE_VERSION_TWO);
            serDe.writeValue(serDeOut, object);
            byte[] serDeOutput = serDeOut.toByteArray();
            assertEquals(object, serDe.readValue(new ByteArrayInputStream(serDeOutput), clazz));
        }
        catch (IOException | AssertionError ex) {
            throw new RuntimeException(ex);
        }
    }

    private static <T> void assertSerializationBackwardsCompatibleV3toV2(Object object)
    {
        Class<?> clazz = object.getClass();
        if (object instanceof FederationRequest)
            clazz = FederationRequest.class;
        else if (object instanceof FederationResponse) {
            clazz = FederationResponse.class;
        }
        try (BlockAllocator allocator = new BlockAllocatorImpl()){
            // check SerDe write, SerDe read
            ByteArrayOutputStream serDeOut = new ByteArrayOutputStream();
            ObjectMapper serDeV3 = VersionedObjectMapperFactory.create(allocator, SERDE_VERSION_THREE);
            ObjectMapper serDeV2 = VersionedObjectMapperFactory.create(allocator, SERDE_VERSION_TWO);
            serDeV3.writeValue(serDeOut, object);
            byte[] serDeOutput = serDeOut.toByteArray();
            assertEquals(object, serDeV2.readValue(new ByteArrayInputStream(serDeOutput), clazz));
        }
        catch (IOException | AssertionError ex) {
            throw new RuntimeException(ex);
        }
    }

    private static <T> void assertSerializationForwardsCompatibleV2toV3(Object object)
    {
        Class<?> clazz = object.getClass();
        if (object instanceof FederationRequest)
            clazz = FederationRequest.class;
        else if (object instanceof FederationResponse) {
            clazz = FederationResponse.class;
        }
        try (BlockAllocator allocator = new BlockAllocatorImpl()){
            // check SerDe write, SerDe read
            ByteArrayOutputStream serDeOut = new ByteArrayOutputStream();
            ObjectMapper serDeV3 = VersionedObjectMapperFactory.create(allocator, SERDE_VERSION_THREE);
            ObjectMapper serDeV2 = VersionedObjectMapperFactory.create(allocator, SERDE_VERSION_TWO);
            serDeV2.writeValue(serDeOut, object);
            byte[] serDeOutput = serDeOut.toByteArray();
            assertEquals(object, serDeV3.readValue(new ByteArrayInputStream(serDeOutput), clazz));
        }
        catch (IOException | AssertionError ex) {
            throw new RuntimeException(ex);
        }
    }
}
