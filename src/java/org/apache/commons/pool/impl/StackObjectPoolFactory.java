/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//pool/src/java/org/apache/commons/pool/impl/StackObjectPoolFactory.java,v 1.1 2001/04/14 16:41:58 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/14 16:41:58 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.pool.impl;

import org.apache.commons.pool.*;
import java.util.Stack;
import java.util.NoSuchElementException;
import java.util.Enumeration;

/**
 * A factory for creating {@link StackObjectPool} instances.
 *
 * @see StackObjectPool
 * @see KeyedObjectPoolFactory
 *
 * @author Rodney Waldhoff
 * @version $Id: StackObjectPoolFactory.java,v 1.1 2001/04/14 16:41:58 rwaldhoff Exp $
 */
public class StackObjectPoolFactory implements ObjectPoolFactory {
    public StackObjectPoolFactory() {
        this((PoolableObjectFactory)null,StackObjectPool.DEFAULT_MAX_SLEEPING,StackObjectPool.DEFAULT_INIT_SLEEPING_CAPACITY);
    }

    public StackObjectPoolFactory(int max) {
        this((PoolableObjectFactory)null,max,StackObjectPool.DEFAULT_INIT_SLEEPING_CAPACITY);
    }

    public StackObjectPoolFactory(int max, int init) {
        this((PoolableObjectFactory)null,max,init);
    }

    public StackObjectPoolFactory(PoolableObjectFactory factory) {
        this(factory,StackObjectPool.DEFAULT_MAX_SLEEPING,StackObjectPool.DEFAULT_INIT_SLEEPING_CAPACITY);
    }

    public StackObjectPoolFactory(PoolableObjectFactory factory, int max) {
        this(factory,max,StackObjectPool.DEFAULT_INIT_SLEEPING_CAPACITY);
    }

    public StackObjectPoolFactory(PoolableObjectFactory factory, int max, int init) {
        _factory = factory;
        _maxSleeping = max;
        _initCapacity = init;
    }

    public ObjectPool createPool() {
        return new StackObjectPool(_factory,_maxSleeping,_initCapacity);
    }

    protected PoolableObjectFactory _factory = null;
    protected int _maxSleeping = StackObjectPool.DEFAULT_MAX_SLEEPING;
    protected int _initCapacity = StackObjectPool.DEFAULT_INIT_SLEEPING_CAPACITY;

}