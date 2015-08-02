/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.access;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.BitSet;

public class PropertyBitSet extends Property {

	public BitSet value = new BitSet();

	public PropertyBitSet() {
	}

	@Override
	public void setValue(Object value) {
		this.value = (BitSet) value;
	}

	@Override
	public boolean equalsValue(Object obj) {
		return value.equals(obj);
	}

	@Override
	public Object copy() {
		return value.clone();
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public void readData(ByteBuf data) throws IOException {
		byte[] bytes = new byte[data.readInt()];
		data.getBytes(0, bytes);
		value = BitSet.valueOf(bytes);
	}

	@Override
	public void writeData(ByteBuf data) throws IOException {
		byte[] bytes = value.toByteArray();
		data.writeInt(bytes.length);
		data.writeBytes(bytes);
	}

}
