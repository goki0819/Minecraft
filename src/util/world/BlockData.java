package util.world;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import util.Node;
import util.nbt.NBTElement;

public class BlockData {
	private String id;
	private Map<String, String> properties;
	
	private BlockData(Node<NBTElement> palette) {
		this.id=NBTElement.sort(palette, "8[Name]").get(0).getElement().getValue().toString();
		this.properties=Node.getElements(NBTElement.sort(palette, "10[Properties]/8")).stream().collect(Collectors.toMap(p->p.getName(), p->p.getValue().toString()));
	}
	
	public String toString() {return id+""+properties;}
	
	public static BlockData[] getBlocks(List<Node<NBTElement>> palettes) {
		BlockData[] bs=new BlockData[palettes.size()];
		for(int i=0;i<bs.length;i++) {
			bs[i]=new BlockData(palettes.get(i));
		}
		return bs;
	}
}
