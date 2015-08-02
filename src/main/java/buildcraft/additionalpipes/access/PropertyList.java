/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.access;


import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

public class PropertyList extends Property {

	public List<Property> value = Lists.newArrayList();

	public PropertyList() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		this.value = (List<Property>) value;
	}

	@Override
	public boolean equalsValue(Object obj) {
		return value.equals(obj);
	}

	@Override
	public Object copy() {
		return Lists.newArrayList(value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public void readData(ByteBuf data) throws IOException {
		byte id = data.readByte();
		int size = data.readInt();
		value = Lists.newArrayListWithCapacity(size);
		for (int i = 0; i < size; i++) {
			Property prop;
			try {
				prop = newProp(id);
			} catch (ReflectiveOperationException e) {
				throw new IOException(e);
			}
			prop.readData(data);
			value.add(prop);
		}
	}

	@Override
	public void writeData(ByteBuf data) throws IOException {
		data.writeByte(!value.isEmpty() ? getId(value.get(0)) : 0);
		data.writeInt(value.size());
		for (Property prop : value) {
			prop.writeData(data);
		}
	}

}
