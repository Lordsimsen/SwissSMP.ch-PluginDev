package ch.swisssmp.utils.nbt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NBTTagList extends NBTBase {
	
	net.minecraft.server.v1_15_R1.NBTTagList value;
	
	public NBTTagList() {
		this.value = new net.minecraft.server.v1_15_R1.NBTTagList();
	}
	
	public boolean add(NBTBase element) {
		return value.add(element.asNMS());
	}
	
	public void add(int index, NBTBase element) {
		value.add(index, element.asNMS());
	}
	
	public boolean addAll(Collection<? extends NBTBase> collection) {
		return value.addAll(collection.stream().map(e->e.asNMS()).collect(Collectors.toList()));
	}
	
	public boolean addAll(int index, Collection<? extends NBTBase> collection) {
		return value.addAll(index, collection.stream().map(e->e.asNMS()).collect(Collectors.toList()));
	}
	
	protected NBTTagList(net.minecraft.server.v1_15_R1.NBTTagList value){
		this.value = value;
	}

	protected net.minecraft.server.v1_15_R1.NBTTagList asNMS() {
		return value;
	}

	public void clear() {
		value.clear();
	}

	public boolean contains(Object o) {
		if(o instanceof NBTBase) {
			return value.contains(((NBTBase)o).asNMS());
		}
		return value.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		for(Object o : c) {
			if(!contains(o)) return false;
		}
		return true;
	}

	public NBTBase get(int index) {
		return NBTBase.get(value.get(index));
	}

	public int indexOf(Object o) {
		if(o instanceof NBTBase) {
			return value.indexOf(((NBTBase)o).asNMS());
		}
		
		return value.indexOf(o);
	}

	public boolean isEmpty() {
		return value.isEmpty();
	}

	public int lastIndexOf(Object o) {
		return value.lastIndexOf(o instanceof NBTBase ? ((NBTBase)o).asNMS() : o);
	}

	public boolean remove(Object o) {
		return value.remove(o instanceof NBTBase ? ((NBTBase)o).asNMS() : o);
	}

	public NBTBase remove(int index) {
		return NBTBase.get(value.remove(index));
	}

	public boolean removeAll(Collection<?> c) {
		return value.removeAll(c.stream().map(e->(e instanceof NBTBase) ? ((NBTBase)e).asNMS() : e).collect(Collectors.toList()));
	}

	public boolean retainAll(Collection<?> c) {
		return value.retainAll(c.stream().map(e->(e instanceof NBTBase) ? ((NBTBase)e).asNMS() : e).collect(Collectors.toList()));
	}

	public NBTBase set(int index, NBTBase element) {
		return NBTBase.get(value.set(index, element.asNMS()));
	}

	public int size() {
		return value.size();
	}

	public List<NBTBase> subList(int fromIndex, int toIndex) {
		return value.subList(fromIndex, toIndex).stream().map(e->NBTBase.get(e)).collect(Collectors.toList());
	}

	public Object[] toArray() {
		return value.stream().map(e->NBTBase.get(e)).toArray();
	}

	public <T> T[] toArray(T[] a) {
		return value.stream().map(e->NBTBase.get(e)).collect(Collectors.toList()).toArray(a);
	}
}
