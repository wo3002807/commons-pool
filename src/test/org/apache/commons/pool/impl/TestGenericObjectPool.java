/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//pool/src/test/org/apache/commons/pool/impl/TestGenericObjectPool.java,v 1.1 2001/04/14 16:42:13 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/14 16:42:13 $
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

import junit.framework.*;
import org.apache.commons.pool.*;

/**
 * @author Rodney Waldhoff
 * @version $Id: TestGenericObjectPool.java,v 1.1 2001/04/14 16:42:13 rwaldhoff Exp $
 */
public class TestGenericObjectPool extends TestCase {
    public TestGenericObjectPool(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestGenericObjectPool.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestGenericObjectPool.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    private GenericObjectPool pool = null;

    public void setUp() {
        pool = new GenericObjectPool(
            new PoolableObjectFactory()  {
                int counter = 0;
                public Object makeObject() { return String.valueOf(counter++); }
                public void destroyObject(Object obj) { }
                public boolean validateObject(Object obj) { return true; }
                public void activateObject(Object obj) { }
                public void passivateObject(Object obj) { }
            }
            );
    }

    public void testBorrow() {
        Object obj0 = pool.borrowObject();
        assertEquals("0",obj0);
        Object obj1 = pool.borrowObject();
        assertEquals("1",obj1);
        Object obj2 = pool.borrowObject();
        assertEquals("2",obj2);
    }

    public void testBorrowReturn() {
        Object obj0 = pool.borrowObject();
        assertEquals("0",obj0);
        Object obj1 = pool.borrowObject();
        assertEquals("1",obj1);
        Object obj2 = pool.borrowObject();
        assertEquals("2",obj2);
        pool.returnObject(obj2);
        obj2 = pool.borrowObject();
        assertEquals("2",obj2);
        pool.returnObject(obj1);
        obj1 = pool.borrowObject();
        assertEquals("1",obj1);
        pool.returnObject(obj0);
        pool.returnObject(obj2);
        obj2 = pool.borrowObject();
        assertEquals("2",obj2);
        obj0 = pool.borrowObject();
        assertEquals("0",obj0);
    }

    public void testNumActiveNumIdle() {
        assertEquals(0,pool.numActive());
        assertEquals(0,pool.numIdle());
        Object obj0 = pool.borrowObject();
        assertEquals(1,pool.numActive());
        assertEquals(0,pool.numIdle());
        Object obj1 = pool.borrowObject();
        assertEquals(2,pool.numActive());
        assertEquals(0,pool.numIdle());
        pool.returnObject(obj1);
        assertEquals(1,pool.numActive());
        assertEquals(1,pool.numIdle());
        pool.returnObject(obj0);
        assertEquals(0,pool.numActive());
        assertEquals(2,pool.numIdle());
    }

    public void testClear() {
        assertEquals(0,pool.numActive());
        assertEquals(0,pool.numIdle());
        Object obj0 = pool.borrowObject();
        Object obj1 = pool.borrowObject();
        assertEquals(2,pool.numActive());
        assertEquals(0,pool.numIdle());
        pool.returnObject(obj1);
        pool.returnObject(obj0);
        assertEquals(0,pool.numActive());
        assertEquals(2,pool.numIdle());
        pool.clear();
        assertEquals(0,pool.numActive());
        assertEquals(0,pool.numIdle());
        Object obj2 = pool.borrowObject();
        assertEquals("2",obj2);
    }

    public void testMaxIdle() {
        pool.setMaxActive(100);
        pool.setMaxIdle(8);
        Object[] active = new Object[100];
        for(int i=0;i<100;i++) {
            active[i] = pool.borrowObject();
        }
        assertEquals(100,pool.numActive());
        assertEquals(0,pool.numIdle());
        for(int i=0;i<100;i++) {
            pool.returnObject(active[i]);
            assertEquals(99 - i,pool.numActive());
            assertEquals((i < 8 ? i+1 : 8),pool.numIdle());
        }
    }

    public void testMaxActive() {
        pool.setMaxActive(3);
        pool.setWhenExhaustedAction(pool.WHEN_EXHAUSTED_FAIL);

        pool.borrowObject();
        pool.borrowObject();
        pool.borrowObject();
        try {
            pool.borrowObject();
            fail("Shouldn't get here.");
        } catch(java.util.NoSuchElementException e) {
            // expected
        }
    }

    public void testEviction() {
        pool.setMaxIdle(500);
        pool.setMaxActive(500);
        pool.setNumTestsPerEvictionRun(100);
        pool.setMinEvictableIdleTimeMillis(500L);
        pool.setTimeBetweenEvictionRunsMillis(500L);

        Object[] active = new Object[500];
        for(int i=0;i<500;i++) {
            active[i] = pool.borrowObject();
        }
        for(int i=0;i<500;i++) {
            pool.returnObject(active[i]);
        }

        try { Thread.currentThread().sleep(2000L); } catch(Exception e) { }
        assert("Should be less than 500 idle, found " + pool.numIdle(),pool.numIdle() < 500);
        try { Thread.currentThread().sleep(600L); } catch(Exception e) { }
        assert("Should be less than 400 idle, found " + pool.numIdle(),pool.numIdle() < 400);
        try { Thread.currentThread().sleep(600L); } catch(Exception e) { }
        assert("Should be less than 300 idle, found " + pool.numIdle(),pool.numIdle() < 300);
        try { Thread.currentThread().sleep(600L); } catch(Exception e) { }
        assert("Should be less than 200 idle, found " + pool.numIdle(),pool.numIdle() < 200);
        try { Thread.currentThread().sleep(600L); } catch(Exception e) { }
        assert("Should be less than 100 idle, found " + pool.numIdle(),pool.numIdle() < 100);
        try { Thread.currentThread().sleep(600L); } catch(Exception e) { }
        assertEquals("Should be zero idle, found " + pool.numIdle(),0,pool.numIdle());

        for(int i=0;i<500;i++) {
            active[i] = pool.borrowObject();
        }
        for(int i=0;i<500;i++) {
            pool.returnObject(active[i]);
        }

        try { Thread.currentThread().sleep(2000L); } catch(Exception e) { }
        assert("Should be less than 500 idle, found " + pool.numIdle(),pool.numIdle() < 500);
        try { Thread.currentThread().sleep(600L); } catch(Exception e) { }
        assert("Should be less than 400 idle, found " + pool.numIdle(),pool.numIdle() < 400);
        try { Thread.currentThread().sleep(600L); } catch(Exception e) { }
        assert("Should be less than 300 idle, found " + pool.numIdle(),pool.numIdle() < 300);
        try { Thread.currentThread().sleep(600L); } catch(Exception e) { }
        assert("Should be less than 200 idle, found " + pool.numIdle(),pool.numIdle() < 200);
        try { Thread.currentThread().sleep(600L); } catch(Exception e) { }
        assert("Should be less than 100 idle, found " + pool.numIdle(),pool.numIdle() < 100);
        try { Thread.currentThread().sleep(600L); } catch(Exception e) { }
        assertEquals("Should be zero idle, found " + pool.numIdle(),0,pool.numIdle());
    }


    public void testThreaded1() {
        pool.setMaxActive(15);
        pool.setMaxIdle(15);
        pool.setMaxWait(1000L);
        TestThread[] threads = new TestThread[20];
        for(int i=0;i<20;i++) {
            threads[i] = new TestThread(pool,100,50);
            Thread t = new Thread(threads[i]);
            t.start();
        }
        for(int i=0;i<20;i++) {
            while(!(threads[i]).complete()) {
                try {
                    Thread.currentThread().sleep(500L);
                } catch(Exception e) {
                    // ignored
                }
            }
            if(threads[i].failed()) {
                fail();
            }
        }
    }

    class TestThread implements Runnable {
        java.util.Random _random = new java.util.Random();
        ObjectPool _pool = null;
        boolean _complete = false;
        boolean _failed = false;
        int _iter = 100;
        int _delay = 50;

        public TestThread(ObjectPool pool) {
            _pool = pool;
        }

        public TestThread(ObjectPool pool, int iter) {
            _pool = pool;
            _iter = iter;
        }

        public TestThread(ObjectPool pool, int iter, int delay) {
            _pool = pool;
            _iter = iter;
            _delay = delay;
        }

        public boolean complete() {
            return _complete;
        }

        public boolean failed() {
            return _failed;
        }

        public void run() {
            for(int i=0;i<_iter;i++) {
                try {
                    Thread.currentThread().sleep((long)_random.nextInt(_delay));
                } catch(Exception e) {
                    // ignored
                }
                Object obj = null;
                try {
                    obj = _pool.borrowObject();
                } catch(java.util.NoSuchElementException e) {
                    _failed = true;
                    _complete = true;
                    break;
                }

                try {
                    Thread.currentThread().sleep((long)_random.nextInt(_delay));
                } catch(Exception e) {
                    // ignored
                }
                _pool.returnObject(obj);
            }
            _complete = true;
        }
    }
}

