package util.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import util.Element;
import util.Node;
import util.nbt.value.ByteArray;
import util.nbt.value.IntArray;
import util.nbt.value.ListValue;
import util.nbt.value.LongArray;
import util.nbt.value.NBTReadableValue;
import util.nbt.value.NumberValue;
import util.nbt.value.StringValue;

public class NBTElement implements Element{
	
	public static final int TAG_End=0;
	public static final int TAG_Byte=1;
	public static final int TAG_Short=2;
	public static final int TAG_Int=3;
	public static final int TAG_Long=4;
	public static final int TAG_Float=5;
	public static final int TAG_Double=6;
	public static final int TAG_Byte_Array=7;
	public static final int TAG_String=8;
	public static final int TAG_List=9;
	public static final int TAG_Compound=10;
	public static final int TAG_Int_Array=11;
	public static final int TAG_Long_Array=12;
	
	private int id;
	private String name;
	private NBTReadableValue<?> value=null;
	
	public NBTElement(InputStream inputStream) throws IOException {
		id=inputStream.read();
		
		if(id==TAG_End)return;
		
		byte[] name_len=new byte[2];
		inputStream.read(name_len);
		byte[] name_bin=new byte[ByteBuffer.wrap(name_len).getShort()];
		inputStream.read(name_bin);
		
		name=new String(name_bin, StandardCharsets.UTF_8);
		
		ini(inputStream);
	}
	/*for ListValue*/
	public NBTElement(InputStream inputStream, ListValue lv) throws IOException {
		this.id=lv.getId();
		
		ini(inputStream);
	}
	
	private void ini(InputStream inputStream) throws IOException {
		if(id==TAG_Compound)return;
		
		if(id==TAG_Byte) {
			value=new NumberValue(inputStream.read());
		}else if(id==TAG_Short) {
			byte[] v=new byte[2];
			inputStream.read(v);
			value=new NumberValue(ByteBuffer.wrap(v).getShort());
		}else if(id==TAG_Int) {
			byte[] v=new byte[4];
			inputStream.read(v);
			value=new NumberValue(ByteBuffer.wrap(v).getInt());
		}else if(id==TAG_Long) {
			byte[] v=new byte[8];
			inputStream.read(v);
			value=new NumberValue(ByteBuffer.wrap(v).getLong());
		}else if(id==TAG_Float) {
			byte[] v=new byte[4];
			inputStream.read(v);
			value=new NumberValue(ByteBuffer.wrap(v).getFloat());
		}else if(id==TAG_Double) {
			byte[] v=new byte[8];
			inputStream.read(v);
			value=new NumberValue(ByteBuffer.wrap(v).getDouble());
		}else if(id==TAG_Byte_Array) {
			value=new ByteArray();
			value.read(inputStream);
		}else if(id==TAG_String) {
			value=new StringValue();
			value.read(inputStream);
		}else if(id==TAG_List) {
			value=new ListValue();
			value.read(inputStream);
		}else if(id==TAG_Int_Array) {
			value=new IntArray();
			value.read(inputStream);
		}else if(id==TAG_Long_Array) {
			value=new LongArray();
			value.read(inputStream);
		}else {
			throw new IOException("Illegal id.["+toString()+"]");
		}
	}
	
	public int getID() {return id;}
	public String getName() {return name;}
	public NBTValue<?> getValue() {return value;}
	public String toString() {
		return "ID:"+getIDName(id)+" ,name:"+name+" ,value:"+value;
	}
	public static String getIDName(int id) {
		switch (id) {
			case TAG_End : return"End";
			case TAG_Byte : return "Byte";
			case TAG_Short : return "Short";
			case TAG_Int : return "Int";
			case TAG_Long : return "Long";
			case TAG_Float : return "Float";
			case TAG_Double : return "Double";
			case TAG_Byte_Array : return "Byte Array";
			case TAG_String : return "String";
			case TAG_List : return "List";
			case TAG_Compound : return "Compound";
			case TAG_Int_Array : return "Int Array";
			case TAG_Long_Array : return "Long Array";
			default : return "Unknown ID "+id;
		}
	}
	
	public static void show(Node<NBTElement> root){show(root, 0);}
	private static void show(Node<NBTElement> node,int n){
		System.out.print(n+" ");
		for(int i=0;i<n;i++)System.out.print("  ");
		if(node.getElement()==null || node.getElement().toString()==null){
			System.out.println("null");
		}else{
			System.out.print(node.getElement().toString()+"\n");
		}
		for(int i=0;i<node.getChildrenSize();i++){
			show(node.getChildren(i), n+1);
		}
	}

	public static List<Node<NBTElement>> sort(Node<NBTElement> root,String match){return sort(root, match.split("/"), 0);}
	private static List<Node<NBTElement>> sort(Node<NBTElement> node,String[] match,int index){//10/10[Level]/9[Sections]/10/12[BlockStates]
		char[] cs=match[index].toCharArray();
		String tag="", name="";
		int state=0;
		for(int i=0;i<cs.length;i++){
			if(cs[i]=='[') state=1;
			else if(cs[i]==']') break;
			else if(state==0) tag+=cs[i];
			else if(state==1){
				name+=cs[i];
			}
		}
//		System.out.println(tag+" "+name);
		
		List<Node<NBTElement>> list=new ArrayList<>();
		for(int i=0;i<node.getChildrenSize();i++){
			Node<NBTElement> cNode=node.getChildren(i);
			NBTElement e=cNode.getElement();
			
			if(e==null)continue;
			
			int tagValue;
			boolean tflag=true;
			try {
				tagValue=Integer.parseInt(tag);
				tflag= tagValue==e.getID();
			}catch(NumberFormatException ex) {
				tflag= tag.length()==0;
			}
			boolean nflag=name.length()>0 ? e.getName().equalsIgnoreCase(name) : true;
			
			if(tflag && nflag){
				if(match.length-1>index){
					list.addAll(sort(cNode, match, index+1));
				}else if(match.length-1==index){
					list.add(cNode);
				}
			}
		}
		return list;
	}

}
