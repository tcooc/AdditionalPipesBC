/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.access;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class PropertyInteger extends Property {

	public static PropertyInteger create(int value) {
		PropertyInteger result = new PropertyInteger();
		result.value = value;
		return result;
	}

	public int value = 0;

	public PropertyInteger() {
	}

	@Override
	public void setValue(Object value) {
		this.value = ((Integer) value).intValue();
	}

	@Override
	public boolean equalsValue(Object obj) {
		return obj instanceof Integer && value == ((Integer) obj).intValue();
	}

	@Override
	public Object copy() {
		return Integer.valueOf(value);
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public void readData(ByteBuf data) throws IOException {
		value = data.readInt();
	}

	@Override
	public void writeData(ByteBuf data) throws IOException {
		data.writeInt(value);
	}

}
