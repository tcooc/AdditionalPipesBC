/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.access;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Ints;

public class PropertyIntArray extends Property {

	public static PropertyIntArray create(int[] value) {
		PropertyIntArray result = new PropertyIntArray();
		result.value = value;
		return result;
	}

	public int[] value = ArrayUtils.EMPTY_INT_ARRAY;

	public PropertyIntArray() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		if (value instanceof List) {
			List<Integer> list = (List<Integer>) value;
			value = Ints.toArray(list);
		}
		this.value = (int[]) value;
	}

	@Override
	public boolean equalsValue(Object obj) {
		if (obj instanceof List) {
			return Ints.asList(value).equals(obj);
		}
		return obj instanceof int[] && Arrays.equals(value, (int[]) obj);
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
		int size = data.readInt();
		if (size > 0) {
			value = new int[size];
			for (int i = 0; i < value.length; i++) {
				value[i] = data.readInt();
			}
		} else {
			value = ArrayUtils.EMPTY_INT_ARRAY;
		}
	}

	@Override
	public void writeData(ByteBuf data) throws IOException {
		data.writeInt(value.length);
		for (int v : value) {
			data.writeInt(v);
		}
	}

}
