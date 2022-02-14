package util.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import util.Node;
import util.nbt.value.ListValue;

public class NBTTokenizer{
	
	private InputStream inputStream;
	
//	public NBTTokenizer(File file) throws IOException {
//		if(file.exists()){
//			inputStream = new GZIPInputStream(new FileInputStream(file));
//		}else{
//			throw new IOException("file is not found.");
//		}
//	}
//	public NBTTokenizer(File file) throws IOException {
//		if(file.exists()){
//			GZIPInputStream gzipInput = new GZIPInputStream(new FileInputStream(file));
//			ByteArrayOutputStream baos=new ByteArrayOutputStream();
//			int len;
//			byte[] b=new byte[1024 * 4];
//			while ((len = gzipInput.read(b)) != -1) {
//                baos.write(b, 0, len);
//            }
//			gzipInput.close();
//			inputStream = new ByteArrayInputStream(baos.toByteArray());
//		}else{
//			throw new IOException("file is not found.");
//		}
//	}
	public NBTTokenizer(InputStream inputStream) throws IOException {
		this.inputStream=inputStream;
	}

	public NBTElement getNextElement() throws IOException {
		if(inputStream.available()==0)return null;
		return new NBTElement(inputStream);
	}
	public NBTElement getNextElement(ListValue lv) throws IOException {
		if(inputStream.available()==0)return null;
		return new NBTElement(inputStream, lv);
	}
	
	private List<Node<NBTElement>> getListChildren(ListValue listValue) throws IOException {
		Node<NBTElement> node=null;
		NBTElement element=null;
		List<Node<NBTElement>> children=new ArrayList<>();
		
		for(int i=0;i<listValue.getValue();i++) {
			element=getNextElement(listValue);
			node=new Node<NBTElement>(element);
			if(element.getID()==NBTElement.TAG_Compound){
				node.addChild(getCompoundChildren());
			}else if(element.getID()==NBTElement.TAG_List){
				ListValue lv=(ListValue)element.getValue();
				node.addChild(getListChildren(lv));
			}
			children.add(node);
		}
		
		return children;
	}
	private List<Node<NBTElement>> getCompoundChildren() throws IOException {
		Node<NBTElement> node=null;
		NBTElement element=getNextElement();
		List<Node<NBTElement>> children=new ArrayList<>();
		
		while(element.getID()!=NBTElement.TAG_End) {
			node=new Node<NBTElement>(element);
			
			if(element.getID()==NBTElement.TAG_Compound){
				node.addChild(getCompoundChildren());
			}else if(element.getID()==NBTElement.TAG_List){
				ListValue lv=(ListValue)element.getValue();
				node.addChild(getListChildren(lv));
			}
			children.add(node);
			element=getNextElement();
		}
		
		return children;
	}
	
	public Node<NBTElement> getRootNode() throws IOException {
		Node<NBTElement> root=new Node<>();
		Node<NBTElement> node=null;
		NBTElement element=null;
		
		while((element = getNextElement()) != null){
			node=new Node<>(element);
			if(element.getID()==NBTElement.TAG_Compound){
				node.addChild(getCompoundChildren());
			}else if(element.getID()==NBTElement.TAG_List){
				ListValue lv=(ListValue)element.getValue();
				node.addChild(getListChildren(lv));
			}else if(element.getID()==NBTElement.TAG_End) {
				return root;
			}
			root.addChild(node);
		}
		
		return root;
	}

}
