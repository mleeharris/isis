/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.legacy.restclient.lib;

import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;

enum RestfulHttpMethodHelper {
    GET(ArgStrategy.QUERY_STRING),
    PUT(ArgStrategy.BODY),
    DELETE(ArgStrategy.QUERY_STRING),
    POST(ArgStrategy.BODY);

    private enum ArgStrategy {
        /**
         * Individually encodes each query arg.
         */
        QUERY_ARGS {
            @Override
            void setUpArgs(final ClientRequestConfigurer clientRequestConfigurer, final JsonRepresentation requestArgs) {
                clientRequestConfigurer.queryArgs(requestArgs);
            }
        },
        /**
         * Sends entire request args as a URL encoded map
         */
        QUERY_STRING {
            @Override
            void setUpArgs(final ClientRequestConfigurer clientRequestConfigurer, final JsonRepresentation requestArgs) {
                clientRequestConfigurer.queryString(requestArgs);
            }
        },
        BODY {
            @Override
            void setUpArgs(final ClientRequestConfigurer clientRequestConfigurer, final JsonRepresentation requestArgs) {
                clientRequestConfigurer.body(requestArgs);
            }
        };
        abstract void setUpArgs(ClientRequestConfigurer clientRequestConfigurer, JsonRepresentation requestArgs);
    }

    private final ArgStrategy argStrategy;

    private RestfulHttpMethodHelper(final ArgStrategy argStrategy) {
        this.argStrategy = argStrategy;
    }

    /**
     * It's a bit nasty that we need to ask for the {@link org.jboss.resteasy.specimpl.ResteasyUriBuilder} as
     * well as the {@link ClientRequest}, but that's because the
     * {@link ClientRequest} does not allow us to setup raw query strings (only
     * query name/arg pairs)
     *
     * @param clientRequestConfigurer
     * @param requestArgs
     */
    public void setUpArgs(final ClientRequestConfigurer clientRequestConfigurer, final JsonRepresentation requestArgs) {
        clientRequestConfigurer.setHttpMethod(this);
        if (requestArgs == null) {
            return;
        }
        if (!requestArgs.isMap()) {
            throw new IllegalArgumentException("requestArgs must be a map; instead got: " + requestArgs);
        }
        argStrategy.setUpArgs(clientRequestConfigurer, requestArgs);
    }

}
