/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.jboss.serial.objectmetamodel;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectIntHashMap;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jboss.serial.classmetamodel.ClassResolver;
import org.jboss.serial.objectmetamodel.safecloning.SafeCloningRepository;
import org.jboss.serial.util.ClassMetaConsts;
import org.jboss.serial.util.StringUtilBuffer;

/**
 * @author clebert suconic
 */
public class ObjectsCache extends DataExport implements ClassMetaConsts
{
	
	
	final TObjectIntHashMap objectsCacheOnWrite = new TObjectIntHashMap(identityHashStrategy);
	final TIntObjectHashMap objectsCacheOnRead = new TIntObjectHashMap();

	ObjectSubstitutionInterface substitution;
    ClassLoader loader=null;
    boolean checkSerializableClass=true;
    SafeCloningRepository safeToReuse;

    JBossSeralizationOutputInterface output;
    JBossSeralizationInputInterface input;
    
    StringUtilBuffer stringBuffer;
    
    ClassResolver resolver;
    
    public ObjectsCache cloneCache()
    {
    	ObjectsCache newCache = new ObjectsCache();
    	newCache.substitution = this.substitution;
    	newCache.loader = this.loader;
    	newCache.checkSerializableClass = this.checkSerializableClass;
    	newCache.safeToReuse = this.safeToReuse;
    	newCache.resolver = this.resolver;
    	newCache.stringBuffer=null;
    	
    	
    	return newCache;
    }
    private ObjectsCache()
    {
    }

    public ObjectsCache(ObjectSubstitutionInterface substitution, ClassLoader loader, SafeCloningRepository safeToReuse, boolean checkSerializableClass, StringUtilBuffer stringBuffer)
    {
        this.loader=loader;
        this.substitution=substitution;
        this.checkSerializableClass=checkSerializableClass;
        this.safeToReuse=safeToReuse;
        this.stringBuffer=stringBuffer;
    }

    public void reset()
    {
    	if (safeToReuse!=null)
		{
    		safeToReuse.clear();
		}
    	objectsCacheOnWrite.clear();
    	objectsCacheOnRead.clear();
    }
    public ClassLoader getLoader() {
    	if (loader==null)
    	{
    		return Thread.currentThread().getContextClassLoader();
    	}
    	else
    	{
    		return loader;
    	}
    }

    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public ObjectSubstitutionInterface getSubstitution() {
        return substitution;
    }

    public void setSubstitution(ObjectSubstitutionInterface substitution) {
        this.substitution = substitution;
    }

    
    
    //final HashSet classesCache = new HashSet();

    public StringUtilBuffer getStringBuffer() {
		return stringBuffer;
	}

	public void setStringBuffer(StringUtilBuffer stringBuffer) {
		this.stringBuffer = stringBuffer;
	}

	// @todo should stop using ObjectReference
    public int findIdInCacheWrite(final Object obj)
    {
    	return objectsCacheOnWrite.get(obj);
    }

    /**
     * @param cacheId
     * @return
     */
    public Object findObjectInCacheRead(int key)
    {
    	return objectsCacheOnRead.get(key);
    }

    public void putObjectInCacheRead(int key, final Object obj)
    {
    	objectsCacheOnRead.put(key,obj);
    }
    
	public void reassignObjectInCacheRead(int key, Object value) {
		objectsCacheOnRead.remove(key);
		putObjectInCacheRead(key,value);
	}
    


    public int putObjectInCacheWrite(final Object obj)
    {
        objectsCacheOnWrite.put(obj,objectsCacheOnWrite.size()+1);
        return objectsCacheOnWrite.size();
    }

    /*public void putClassMetaData(final ClassMetaData metaData)
    {
        classesCache.add(metaData);
    }*/

    /* (non-Javadoc)
     * @see org.jboss.serial.objectmetamodel.DataExport#writeMyself(java.io.DataOutput)
     */
//    public void writeMyself(DataOutput output) throws IOException
//    {
//
//        /*output.writeInt(classesCache.size());
//        Iterator iter = classesCache.iterator();
//        while (iter.hasNext())
//        {
//            ClassMetaData metaData = (ClassMetaData )iter.next();
//            output.writeUTF(metaData.getClassName());
//        } */
//
//        output.writeInt(objectsCache.size());
//        Iterator iter = objectsCache.entrySet().iterator();
//        while (iter.hasNext())
//        {
//            Map.Entry entry = (Map.Entry)iter.next();
//            ObjectReference hashKey = (ObjectReference)entry.getKey();
//            ObjectDescription value = (ObjectDescription)entry.getValue();
//            hashKey.writeMyself(output);
//            value.writeMyself(output);
//        }
//
//    }
//
//    /* (non-Javadoc)
//     * @see org.jboss.serial.objectmetamodel.DataExport#readMyself(java.io.DataInput)
//     */
//    public void readMyself(DataInput input) throws IOException
//    {
//
//        int size=input.readInt();
//
//        for (int i=0;i<size;i++)
//        {
//            //Integer key = Integer.valueOf(input.readInt());
//
//            ObjectReference key = new ObjectReference();
//            key.readMyself(input);
//            ObjectDescription description = new ObjectDescription(null,this);
//            description.readMyself(input);
//
//            objectsCache.put(key,description);
//        }
//
//    }

    public SafeCloningRepository getSafeToReuse() {
        return safeToReuse;
    }

    public boolean isCheckSerializableClass() {
        return checkSerializableClass;
    }

    public void setCheckSerializableClass(boolean checkSerializableClass) {
        this.checkSerializableClass = checkSerializableClass;
    }

//    public void flush() throws IOException
//    {
//        Iterator iter = objectsCache.entrySet().iterator();
//        while (iter.hasNext())
//        {
//            Map.Entry entry = (Map.Entry)iter.next();
//            ObjectDescription value = (ObjectDescription)entry.getValue();
//            value.flush(false);
//        }
//    }

    public JBossSeralizationOutputInterface getOutput() {
        return output;
    }

    public void setOutput(JBossSeralizationOutputInterface output) {
        this.output = output;
    }

    public JBossSeralizationInputInterface getInput() {
        return input;
    }

    public void setInput(JBossSeralizationInputInterface input) {
        this.input = input;
    }

	public ClassResolver getClassResolver() {
		return resolver;
	}
	
	public void setClassResolver(ClassResolver resolver)
	{
		this.resolver=resolver;
	}
    
    /** Required operations to work well with ObjectsCache */
    public interface JBossSeralizationOutputInterface extends ObjectOutput
    {
    	/** Extracts the integer of an object, and add the output */
        public void addObjectReference(int reference) throws IOException;
        public void openObjectDefinition() throws IOException;
        public void closeObjectDefinition() throws IOException;
        public void writeByteDirectly(byte parameter) throws IOException;
        public boolean isCheckSerializableClass();
        public void saveImmutable(ObjectsCache cache, Object obj) throws IOException;
    }

    /** Required operations to work well with ObjectsCache */
    public interface JBossSeralizationInputInterface extends ObjectInput
    {
        public int readObjectReference() throws IOException;
        public byte readByteDirectly() throws IOException;
        public Object readImmutable(byte byteDescription,ObjectsCache cache) throws IOException;
    }



}
