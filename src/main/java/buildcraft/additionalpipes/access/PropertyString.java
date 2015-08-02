/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.access;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import cpw.mods.fml.common.network.ByteBufUtils;

public class PropertyString extends Property {

	public String value = "";

	public PropertyString() {
	}

	@Override
	public void setValue(Object value) {
		this.value = (String) value;
	}

	@Override
	public boolean equalsValue(Object obj) {
		return value.equals(obj);
	}

	@Override
	public Object copy() {
		return value;// String is immutable
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public void readData(ByteBuf data) throws IOException {
		value = ByteBufUtils.readUTF8String(data);
	}

	@Override
	public void writeData(ByteBuf data) throws IOException {
		ByteBufUtils.writeUTF8String(data, value);
	}

}
