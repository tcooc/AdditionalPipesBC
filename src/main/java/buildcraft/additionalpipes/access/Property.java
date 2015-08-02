/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.access;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public abstract class Property {

	private static BiMap<Class<? extends Property>, Byte> classToIdMap = HashBiMap.create();

	static {
		addClassMapping(PropertyBoolean.class);
		addClassMapping(PropertyInteger.class);
		addClassMapping(PropertyString.class);
		addClassMapping(PropertyItemStack.class);
		addClassMapping(PropertyBitSet.class);
		addClassMapping(PropertyIntArray.class);
		addClassMapping(PropertyStrArray.class);
		addClassMapping(PropertyList.class);
		addClassMapping(PropertyMap.class);
	}

	protected static void addClassMapping(Class<? extends Property> clazz) {
		classToIdMap.put(clazz, (byte) classToIdMap.size());
	}

	public static Property newProp(byte id) throws InstantiationException, IllegalAccessException {
		return classToIdMap.inverse().get(id).newInstance();
	}

	public static byte getId(Property prop) {
		return classToIdMap.get(prop.getClass());
	}

	public static Property readPacket(ByteBuf data) throws IOException {
		try {
			Property result = newProp(data.readByte());
			result.readData(data);
			return result;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writePacket(Property prop, ByteBuf data) throws IOException {
		data.writeByte(getId(prop));
		prop.writeData(data);
	}

	public int index;

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Property && equalsValue(((Property) obj).copy());
	}

	public abstract void setValue(Object value);
	public abstract boolean equalsValue(Object obj);
	public abstract Object copy();
	public abstract void readData(ByteBuf data) throws IOException;
	public abstract void writeData(ByteBuf data) throws IOException;

}
