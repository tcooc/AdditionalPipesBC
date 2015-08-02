/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.access;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;

public class PropertyMap extends Property {

	public Map<Property, Property> value = Maps.newHashMap();
	private ImmutableMap<Property, Property> prevValue = ImmutableMap.of();

	public PropertyMap() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		this.value = (Map<Property, Property>) value;
	}

	@Override
	public boolean equalsValue(Object obj) {
		return value.equals(obj);
	}

	@Override
	public Object copy() {
		return Maps.newHashMap(value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public void readData(ByteBuf data) throws IOException {
		int size = data.readInt();
		for (int i = 0; i < size; i++) {
			boolean removed = data.readBoolean();
			Property key = Property.readPacket(data);
			if (removed) {
				value.remove(key);
			} else {
				value.put(key, Property.readPacket(data));
			}
		}
	}

	@Override
	public void writeData(ByteBuf data) throws IOException {
		MapDifference<Property, Property> diff = Maps.difference(prevValue, value);

		Map<Property, ValueDifference<Property>> changed = diff.entriesDiffering();
		Map<Property, Property> added = diff.entriesOnlyOnRight();
		Map<Property, Property> removed = diff.entriesOnlyOnLeft();
		data.writeInt(changed.size() + added.size() + removed.size());

		for (Entry<Property, ValueDifference<Property>> e : changed.entrySet()) {
			data.writeBoolean(false);
			Property.writePacket(e.getKey(), data);
			Property.writePacket(e.getValue().rightValue(), data);
		}
		for (Entry<Property, Property> e : added.entrySet()) {
			data.writeBoolean(false);
			Property.writePacket(e.getKey(), data);
			Property.writePacket(e.getValue(), data);
		}
		for (Entry<Property, Property> e : removed.entrySet()) {
			data.writeBoolean(true);
			Property.writePacket(e.getKey(), data);
		}

		prevValue = ImmutableMap.copyOf(value);
	}

}
