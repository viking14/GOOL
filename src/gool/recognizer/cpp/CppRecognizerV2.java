package gool.recognizer.cpp;

import gool.ast.core.ArrayAccess;
import gool.ast.core.ArrayNew;
import gool.ast.core.Assign;
import gool.ast.core.BinaryOperation;
import gool.ast.core.Block;
import gool.ast.core.Case;
import gool.ast.core.CastExpression;
import gool.ast.core.Catch;
import gool.ast.core.ClassDef;
import gool.ast.core.ClassNew;
import gool.ast.core.CompoundAssign;
import gool.ast.core.Constant;
import gool.ast.core.Constructor;
import gool.ast.core.Dependency;
import gool.ast.core.DoWhile;
import gool.ast.core.Expression;
import gool.ast.core.Field;
import gool.ast.core.FieldAccess;
import gool.ast.core.For;
import gool.ast.core.GoolCall;
import gool.ast.core.Identifier;
import gool.ast.core.If;
import gool.ast.core.MainMeth;
import gool.ast.core.MemberSelect;
import gool.ast.core.Meth;
import gool.ast.core.MethCall;
import gool.ast.core.Modifier;
import gool.ast.core.NewInstance;
import gool.ast.core.Node;
import gool.ast.core.Operator;
import gool.ast.core.RecognizedDependency;
import gool.ast.core.Return;
import gool.ast.core.Statement;
import gool.ast.core.Switch;
import gool.ast.core.This;
import gool.ast.core.Try;
import gool.ast.core.UnaryOperation;
import gool.ast.core.UnrecognizedDependency;
import gool.ast.core.VarDeclaration;
import gool.ast.core.While;
import gool.ast.system.SystemOutDependency;
import gool.ast.system.SystemOutPrintCall;
import gool.ast.type.IType;
import gool.ast.type.TypeArray;
import gool.ast.type.TypeBool;
import gool.ast.type.TypeChar;
import gool.ast.type.TypeClass;
import gool.ast.type.TypeDecimal;
import gool.ast.type.TypeGoolLibraryClass;
import gool.ast.type.TypeInt;
import gool.ast.type.TypeMethod;
import gool.ast.type.TypeString;
import gool.ast.type.TypeVar;
import gool.ast.type.TypeVoid;
import gool.generator.GeneratorHelper;
import gool.generator.common.Platform;
import gool.generator.java.JavaPlatform;
import gool.parser.cpp.*;
import gool.recognizer.common.GoolLibraryClassAstBuilder;
import gool.recognizer.common.RecognizerMatcher;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@SuppressWarnings("unchecked")
public class CppRecognizerV2 implements CPPParserVisitor, CPPParserTreeConstants {

	/**
	 * Les différents niveau d'affichage de debug :
	 * 0 -> aucun affichage
	 * 1 -> affiche jjtThis.Value et jjtThis.Type des noeuds où elles sont non null.
	 * 2 -> affiche jjtThis.Value de tous les noeuds 
	 * 		et jjtThis.Type où elle est non null.
	 * 3 -> affiche jjtThis.Value et jjtThis.Type.
	 */
	public final int debug_level = 0;

	public final int RETURN_OK = 1;

	// AST produit par le parser C++
	private List<SimpleNode> AST;

	// Collection des classes <=> AST GOOL
	private Collection<ClassDef> goolClasses = new ArrayList<ClassDef> ();

	// Getter sur L'AST GOOL
	public final Collection<ClassDef> getGoolClasses() {
		return goolClasses;
	}


	// Langage output (fixé à JAVA pour les tests)
	private Platform defaultPlatform = JavaPlatform.getInstance();
	//private Platform defaultPlatform = CppPlatform.getInstance();
	//private Platform defaultPlatform = PythonPlatform.getInstance();

	// Pointeurs sur la "classe active",
	//ie la classe à laquelle les éléments qui sont en trian d'être visités appartiennent
	//private ClassDef classActive;
	private Stack<ClassDef> stackClassActives = new Stack<ClassDef>();
	private Meth methActive = null;
	private Modifier accesModifierActive = Modifier.PUBLIC;

	// un cache pour Savoir ce qui est importé.
	private Collection<String> importCache = new ArrayList<String>();


	// Constructeur
	public CppRecognizerV2 (){
		this.AST=CPPParser.getCppAST();
	}

	// Getter sur l'ast c++
	public List<SimpleNode> getAST (){
		return AST;
	}

	// Un mode debug pour l'affichage des noeuds

	public void debug(String node, Object jjtThis_Value,  Object jjtThis_Type){
		switch(debug_level){

		case 0 :
			break;

		case 1 :
			if(jjtThis_Value != null &&  jjtThis_Type != null)
				System.out.println("[" + node + "] : " + "Value = \'" + jjtThis_Value + "\'" + " , Type = \'" + jjtThis_Type + "\'");
			else if (jjtThis_Value != null)
				System.out.println("[" + node + "] : " + "Value = \'" + jjtThis_Value + "\'");
			else if (jjtThis_Type != null)
				System.out.println("[" + node + "] : " + "Type = \'" + jjtThis_Type + "\'");
			break ;

		case 2 :
			if(jjtThis_Type != null)
				System.out.println("[" + node + "] : " + "Value = \'" + jjtThis_Value + "\'" + " , Type = \'" + jjtThis_Type + "\'");
			else
				System.out.println("[" + node + "] : " + "Value = \'" + jjtThis_Value + "\'");
			break ;

		case 3 :
			System.out.println("[" + node + "] : " + "Value = \'" + jjtThis_Value + "\'" + " , Type = \'" + jjtThis_Type + "\'");
			break;	
		}

	}

	public static void main (String args[]){
		CppRecognizerV2 cppr = new CppRecognizerV2();
		List<SimpleNode> ast;

		try{
			ast = cppr.getAST();
			for (SimpleNode a : ast)
				a.dump("");
		}
		catch (Exception e){return;}

		RecognizerMatcher.init("cpp");
		
		for (SimpleNode a : ast)
			cppr.visit(a, 0);
		try {
			GeneratorHelper.printClassDefs(cppr.getGoolClasses());
		} catch (FileNotFoundException e) {}
	}

	private void getUnrocognizedPart(Token begin, Token end, String prefix){
		String toPrint="";
		while (!(begin.beginLine == end.beginLine && begin.beginColumn == end.beginColumn)){
			/*toPrint+=begin.image;
			boolean sautDeLigne=false;
			for (int i=0;i<(begin.next.beginLine-begin.endLine);i++){
				toPrint+="\n";
				sautDeLigne=true;
			}
			if (sautDeLigne)
				for (int i=0;i<begin.next.beginColumn-1;i++)
					toPrint+=" ";
			else
				for (int i=0;i<(begin.next.beginColumn-begin.endColumn-1);i++)
					toPrint+=" ";*/
			toPrint+=begin.image+" ";
			begin=begin.next;
		}
		toPrint+=end.image;
		System.out.println("WARNING: \" "+prefix+toPrint+" \" dans "+getLocationError()+" (ligne "+begin.beginLine+") a été ignoré car non reconnu par GOOL !");
	}

	private void getUnrocognizedPart(Token begin, Token end){
		getUnrocognizedPart(begin, end, "");
	}

	private void setUnrocognizedPart(String toPrint, Token begin){
		System.out.println("WARNING: \" "+toPrint+" \" dans "+getLocationError()+" (ligne "+begin.beginLine+") a été ignoré car non reconnu par GOOL !");
	}

	private String getLocationError (){
		if (methActive != null){
			return "la fonction "+methActive.getName();
		}
		else
			return "la classe "+stackClassActives.peek().getName();
	}

	private Object returnChild (int typeNode, SimpleNode node, int pos, Object data){
		if (node.jjtGetNumChildren() < pos+1)
			return null;
		else if (node.jjtGetChild(pos).jjtGetId() != typeNode)
			return null;
		else
			return visit((SimpleNode) node.jjtGetChild(pos), data);
	}

	@Override
	public Object visit(SimpleNode node, Object data) {
		return node.jjtAccept(this, data);
	}

	private String createClassNameFromFilename(Object o){
		String filename = (String) o;
		String className = filename.split("\\.")[0];		
		return className.substring(0, 1).toUpperCase() + className.substring(1);
	}

	private boolean testChild (SimpleNode node, int n, String value){
		if (node.jjtGetId() == n){
			if (value == null)
				return true;
			else if (node.jjtGetValue() != null && node.jjtGetValue().toString().compareTo(value) == 0)
				return true;
		}
		boolean toReturn = false;
		for (int i=0;i<node.jjtGetNumChildren();i++)			
			toReturn |= testChild ((SimpleNode) node.jjtGetChild(i), n, value);
		return toReturn;
	}

	private boolean testChild (SimpleNode node, int n){
		return testChild (node,n, null);
	}

	private IType convertIType (String type){		
		if (type == null)
			return null;

		if (type.compareTo("int") == 0){return TypeInt.INSTANCE;}
		else if (type.compareTo("void") == 0){return TypeVoid.INSTANCE;}
		else if (type.compareTo("char") == 0){return TypeChar.INSTANCE;}
		else if (type.compareTo("short") == 0){return TypeInt.INSTANCE; /* short -> int */ }
		else if (type.compareTo("long") == 0){return TypeInt.INSTANCE; /* long -> int */ }
		else if (type.compareTo("float") == 0){return TypeDecimal.INSTANCE; /* float -> decimal */ }
		else if (type.compareTo("double") == 0){return TypeDecimal.INSTANCE; /* double -> decimal */ }
		else if (type.compareTo("signed") == 0){return TypeInt.INSTANCE; /* signed -> int */ }
		else if (type.compareTo("unsigned") == 0){return TypeInt.INSTANCE; /*unsigned -> int */ }
		else if (type.compareTo("boolean") == 0){return TypeBool.INSTANCE;}
		else return null;
	}

	private Modifier convertModToGoolMod (String mod){
		if (mod.compareTo("const") == 0){return Modifier.FINAL;}
		else if (mod.compareTo("volatile") == 0){return Modifier.VOLATILE;}
		else if (mod.compareTo("static") == 0){return Modifier.STATIC;}
		else if (mod.compareTo("public") == 0){return Modifier.PUBLIC;}
		else if (mod.compareTo("protected") == 0){return Modifier.PROTECTED;}
		else if (mod.compareTo("private") == 0){return Modifier.PRIVATE;}
		else if (mod.compareTo("virtual") == 0){
			stackClassActives.peek().addModifier(Modifier.ABSTRACT);
			return Modifier.ABSTRACT;
		}
		else return null;
	}

	private Expression getBinaryExpression (SimpleNode node, int i, List<?> listOpe, Object data){
		if (listOpe == null)
			return (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
		else if (i == 0)
			return (Expression) visit((SimpleNode) node.jjtGetChild(i),data);
		else {
			String sym = (String) listOpe.get(i-1);
			Expression e1 = (Expression) visit((SimpleNode) node.jjtGetChild(i),data);
			if (e1 == null){return null;}
			return new BinaryOperation(convertSymToOpe(sym),getBinaryExpression (node, i-1, listOpe, data),e1,e1.getType(),sym);			
		}		
	}

	private Operator convertSymToOpe (String sym){
		if (sym.compareTo("+") == 0){return Operator.PLUS;}
		else if (sym.compareTo("-") == 0){return Operator.MINUS;}
		else if (sym.compareTo("*") == 0){return Operator.MULT;}
		else if (sym.compareTo("/") == 0){return Operator.DIV; }
		else if (sym.compareTo("%") == 0){return Operator.UNKNOWN;}
		else if (sym.compareTo("==") == 0){return Operator.EQUAL;}
		else if (sym.compareTo("!=") == 0){return Operator.NOT_EQUAL;}
		else if (sym.compareTo(">") == 0){return Operator.GT;}
		else if (sym.compareTo("<") == 0){return Operator.LT;}
		else if (sym.compareTo(">=") == 0){return Operator.GEQ;}
		else if (sym.compareTo("<=") == 0){return Operator.LEQ;}
		else if (sym.compareTo("&&") == 0){return Operator.AND;}
		else if (sym.compareTo("||") == 0){return Operator.OR;}
		else if (sym.compareTo("!") == 0){return Operator.NOT;}
		else return Operator.UNKNOWN;
	}

	private boolean isFunctionPrint (SimpleNode node, Object data){
		if (node.jjtGetNumChildren() < 3)
			return false;

		boolean testAdd=true;
		for (int i=0;i<node.jjtGetNumChildren();i++)
			if (node.jjtGetChild(i).jjtGetId() != JJTADDITIVE_EXPRESSION)
				testAdd=false;
		if (!testAdd){return false;}

		try {
			String cL = ((Identifier) visit((SimpleNode) node.jjtGetChild(0),data)).getName();
			String cR = ((Identifier) visit((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1),data)).getName();
			return cL.compareTo("cout") == 0 && cR.compareTo("endl") == 0;
		} catch (Exception e){return false;}
	}

	private Expression getExpressionPrint (SimpleNode node, int i, Object data){
		if (i == node.jjtGetNumChildren()-2)
			return (Expression) visit((SimpleNode) node.jjtGetChild(i),data);
		else{
			Expression e1 = (Expression) visit((SimpleNode) node.jjtGetChild(i),data);
			return new BinaryOperation(Operator.PLUS,e1,getExpressionPrint (node, i+1,data),e1.getType(),"+");
		}
	}

	private Expression getBooleanExpression (SimpleNode node, Object data){
		if (node.jjtGetNumChildren() == 2){
			Operator o = convertSymToOpe((String) node.jjtGetValue());
			Expression left = (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
			Expression right = (Expression) visit((SimpleNode) node.jjtGetChild(1),data);			
			if (left == null || right == null){return null;}			
			return new BinaryOperation(o, left, right, TypeBool.INSTANCE, (String) node.jjtGetValue());
		}
		else if (node.jjtGetNumChildren() == 1){
			return (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
		}
		else return null;
	}

	private boolean testDeclarationTableau (SimpleNode node){
		if (testChild(node, JJTDECLARATOR_SUFFIXES))
			return (!testChild(node, JJTDECLARATOR_SUFFIXES,"()"));
		return false;
	}

	private Expression getListDim (SimpleNode node, int i, Object data){
		if (i == 1){
			Expression id = (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
			Expression index = (Expression) visit((SimpleNode) node.jjtGetChild(1),data);
			if (id == null || index == null){return null;}
			return new ArrayAccess(id, index);
		}
		else{
			Expression index = (Expression) visit((SimpleNode) node.jjtGetChild(i),data);
			if (index == null){return null;}
			return new ArrayAccess(getListDim(node, i-1, data), index);
		}
	}

	private Object setMethActive (String className, String name){
		Iterator<ClassDef> it = goolClasses.iterator();
		while (it.hasNext()){
			ClassDef tmp = it.next();
			if (tmp.getName().compareTo(className) == 0){

				Meth m = tmp.getMethod(name);
				if (m == null){return null;}
				else
					methActive=m;

				return RETURN_OK;
			}
		}
		return null;
	}

	private ClassDef classExist (String className){
		Iterator<ClassDef> it = goolClasses.iterator();
		while (it.hasNext()){
			ClassDef tmp = it.next();
			if (tmp.getName().compareTo(className) == 0){
				return tmp;
			}
		}
		return null;
	}

	/******************************************************************************************/

	@Override
	public Object visit(TRANSLATION_UNIT node, Object data) {
		debug("TRANSLATION_UNIT", node.jjtGetValue(), node.jjtGetType());
		ClassDef unitaryClass = classExist(createClassNameFromFilename(node.jjtGetValue()));
		if (unitaryClass == null){
			unitaryClass = new ClassDef(Modifier.PUBLIC, createClassNameFromFilename(node.jjtGetValue()), defaultPlatform);
			goolClasses.add(unitaryClass);
		}
		stackClassActives.push(unitaryClass);
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(EXTERNAL_DECLARATION node, Object data) {
		debug("EXTERNAL_DECLARATION", node.jjtGetValue(), node.jjtGetType());
		for (int i=0;i<node.jjtGetNumChildren();i++){
			if (node.jjtGetChild(i).jjtAccept(this, data) == null)
				getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
		}		
		return null;
	}

	@Override
	public Object visit(FUNCTION_DEFINITION node, Object data) {
		debug("FUNCTION_DEFINITION", node.jjtGetValue(), node.jjtGetType());
		String className = (String) returnChild(JJTFUNCTION_DECLARATOR, node, 1, "GET_SCOPE");
		if (className != null){
			String name = (String) returnChild(JJTFUNCTION_DECLARATOR, node, 1, "GET_NAME");
			if (name == null){return null;}
			if (setMethActive(className.replaceAll("::", ""),name) == null){return null;}
			visit((FUNC_DECL_DEF) node.jjtGetChild(2), data);
			methActive=null;
			return RETURN_OK;
		}

		IType type = (IType) returnChild(JJTDECLARATION_SPECIFIERS, node, 0, "GET_TYPE");
		if (type == null){return null;}
		if (node.jjtGetChild(1).jjtGetChild(0).jjtGetId()==JJTPTR_OPERATOR && node.jjtGetChild(1).jjtGetChild(0).jjtGetValue() != null){
			if (type.equals(TypeChar.INSTANCE))
				type=TypeString.INSTANCE;
			else
				return null;
		}

		String name = (String) returnChild(JJTFUNCTION_DECLARATOR, node, 1, "GET_NAME");
		if (name == null){return null;}

		Collection <Modifier> cm = (Collection <Modifier>) visit((DECLARATION_SPECIFIERS) node.jjtGetChild(0), "GET_MODIFIERS");
		if (cm == null){return null;}

		Meth m;
		List<VarDeclaration> listVD=null;
		if (testChild(node, JJTPARAMETER_LIST)){
			listVD = (List<VarDeclaration>) returnChild(JJTFUNCTION_DECLARATOR, node, 1, "GET_PARAMS");
		}
		if (listVD == null){listVD=new ArrayList<VarDeclaration>();}

		if (name.compareTo("main") == 0 && type == TypeInt.INSTANCE && ((listVD.size() == 2 
				&& listVD.get(0).getType() == TypeInt.INSTANCE && listVD.get(1).getType() == TypeString.INSTANCE) || (listVD.size() == 0))){ 
			m = new MainMeth();
		}
		else{
			m = new Meth(type, name);
			for (VarDeclaration vd : listVD)
				m.addParameter(vd);

			if (testChild(node, JJTEXCEPTION_SPEC)){
				List<IType> lt = (List<IType>) returnChild(JJTFUNCTION_DECLARATOR, node, 1, "GET_EXCEP");
				if (lt != null)
					for (IType t : lt)
						m.addThrowStatement(t);
			}
		}

		m.setModifiers(cm);
		m.addModifier(accesModifierActive);
		stackClassActives.peek().addMethod(m);
		methActive=m;
		visit((FUNC_DECL_DEF) node.jjtGetChild(2), data);
		methActive=null;
		return RETURN_OK;
	}

	@Override
	public Object visit(FUNC_DECL_DEF node, Object data) {
		debug("FUNC_DECL_DEF", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() > 0){
			Block blockToAdd = (Block) visit((SimpleNode) node.jjtGetChild(0),data);

			if (blockToAdd != null)
				methActive.addStatement(blockToAdd);
		}
		return RETURN_OK;			
	}

	@Override
	public Object visit(LINKAGE_SPECIFICATION node, Object data) {
		debug("LINKAGE_SPECIFICATION", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(DECLARATION node, Object data) {
		debug("DECLARATION", node.jjtGetValue(), node.jjtGetType());
		if (testChild(node, JJTSTORAGE_CLASS_SPECIFIER,"UNKNOW"))
			return null;

		// Cas d'une déclaration de classe
		if (testChild(node, JJTCLASS_SPECIFIER))	
			return returnChild(JJTDECLARATION_SPECIFIERS, node, 0, data);
		// Cas d'une déclaration de variable
		else if (testChild(node, JJTDECLARATION_SPECIFIERS) && testChild(node, JJTINIT_DECLARATOR_LIST)){
			Block blockToReturn = new Block();
			IType type = (IType) returnChild(JJTDECLARATION_SPECIFIERS, node, 0, data);
			if (type == null){return null;}
			if (testChild((SimpleNode) node.jjtGetChild(1), JJTPTR_OPERATOR,"*")){
				if (type.equals(TypeChar.INSTANCE))
					type=TypeString.INSTANCE;
				else
					return null;
			}

			Collection <Modifier> cm = (Collection <Modifier>) visit((DECLARATION_SPECIFIERS) node.jjtGetChild(0), "GET_MODIFIERS");
			if (cm == null || cm.contains(Modifier.ABSTRACT)){cm = new ArrayList<Modifier>();}

			SimpleNode nodeDecList = (SimpleNode) node.jjtGetChild(1); // <- INIT_DECLARATOR_LIST
			for(int i=0;i<nodeDecList.jjtGetNumChildren();i++){
				String name = (String) returnChild(JJTINIT_DECLARATOR, nodeDecList, i, "GET_NAME");						
				if (name == null){return null;}

				Expression def = null;
				if (nodeDecList.jjtGetChild(i).jjtGetNumChildren() > 1 && nodeDecList.jjtGetChild(i).jjtGetChild(1).jjtGetId() == JJTINITIALIZER){
					def = (Expression) visit((SimpleNode) nodeDecList.jjtGetChild(i).jjtGetChild(1), data);
					if (def == null)
						getUnrocognizedPart(((SimpleNode) nodeDecList.jjtGetChild(i).jjtGetChild(1)).jjtGetFirstToken(), ((SimpleNode) nodeDecList.jjtGetChild(i).jjtGetChild(1)).jjtGetLastToken(),"= ");
				}

				IType saveType = type;
				if (testDeclarationTableau((SimpleNode) nodeDecList.jjtGetChild(i))){
					int dim = (Integer) visit((SimpleNode) nodeDecList.jjtGetChild(i),"GET_DIM");

					for (int j=0;j<dim;j++){
						type=new TypeArray(type);
					}					
					List<Expression> le = (List<Expression>) visit((SimpleNode) nodeDecList.jjtGetChild(i),"GET_DIM_VAL");
					if (le.size() > 0){
						if (dim > 1){return null;} // Maximum un dimension sinon problème de génération de code ...
						def=new ArrayNew(saveType, le, le);				
					}					
				}				

				// Déclaration des attributs de la classe
				if (node.jjtGetParent().jjtGetId() == JJTEXTERNAL_DECLARATION){
					cm.add(Modifier.PRIVATE);
					stackClassActives.peek().addField(new Field(cm, name, type, def));
				}			
				// Initialisation avec constructeur non vide : Test t(1);
				else if (nodeDecList.jjtGetChild(i).jjtGetNumChildren() > 1 && nodeDecList.jjtGetChild(i).jjtGetChild(1).jjtGetId() == JJTEXPRESSION_LIST){					
					VarDeclaration vd = new VarDeclaration(type, name);
					vd.setModifiers(cm);
					NewInstance ni = new NewInstance(vd);
					List<Expression> le =  (List<Expression>) visit((SimpleNode) nodeDecList.jjtGetChild(i).jjtGetChild(1),data);
					if (le == null){return null;}
					for (Expression e : le)
						ni.addParameter(e);
					blockToReturn.addStatement(ni);
				}
				// Initialisation avec constructeur vide : Test t() ou Test t = new Test (...);
				else if (type instanceof TypeClass){
					VarDeclaration vd = new VarDeclaration(type, name);
					vd.setModifiers(cm);
					NewInstance ni = new NewInstance(vd);
					if (nodeDecList.jjtGetChild(i).jjtGetNumChildren() > 1 && nodeDecList.jjtGetChild(i).jjtGetChild(1).jjtGetId() == JJTINITIALIZER && def != null)
						ni.addParameters(((ClassNew) def).getParameters());
					blockToReturn.addStatement(ni);
				}
				// Initialisation simple : int i; ou int i=1;
				else{
					VarDeclaration vd = new VarDeclaration(type, name);
					vd.setInitialValue(def);					
					vd.setModifiers(cm);
					blockToReturn.addStatement(vd);
				}

				type=saveType;
			}

			return blockToReturn;
		}		
		return null;
	}

	@Override
	public Object visit(TYPE_MODIFIERS node, Object data) {
		debug("TYPE_MODIFIERS", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetValue() != null){
			return convertModToGoolMod(node.jjtGetValue().toString());
		}
		else if (node.jjtGetNumChildren() != 0)
			return node.jjtGetChild(0).jjtAccept(this, data);
		else{
			getUnrocognizedPart(node.jjtGetFirstToken(), node.jjtGetLastToken());
			return null;
		}
	}

	@Override
	public Object visit(DECLARATION_SPECIFIERS node, Object data) {
		debug("DECLARATION_SPECIFIERS", node.jjtGetValue(), node.jjtGetType());
		if (data.toString().compareTo("GET_MODIFIERS") == 0){
			Collection <Modifier> toReturn = new ArrayList<Modifier>();
			for (int i=0;node.jjtGetChild(i).jjtGetId() == JJTTYPE_MODIFIERS;i++){
				Modifier m = (Modifier) visit((SimpleNode) node.jjtGetChild(i), data);
				if (m == null)
					getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
				else
					toReturn.add(m);
			}
			return toReturn;			
		}
		else{
			int d=0;
			while (node.jjtGetChild(d).jjtGetId() == JJTTYPE_MODIFIERS){d++;}
			if (testChild(node, JJTCLASS_SPECIFIER))
				return returnChild(JJTCLASS_SPECIFIER, node, d, data);
			else if ((testChild(node, JJTBUILTIN_TYPE_SPECIFIER))){
				return returnChild(JJTBUILTIN_TYPE_SPECIFIER, node, d, data);
			}
			else if ((testChild(node, JJTQUALIFIED_TYPE))){
				return returnChild(JJTQUALIFIED_TYPE, node, d, data);
			}
		}
		return null;
	}

	@Override
	public Object visit(SIMPLE_TYPE_SPECIFIER node, Object data) {
		debug("SIMPLE_TYPE_SPECIFIER", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(SCOPE_OVERRIDE_LOOKAHEAD node, Object data) {
		debug("SCOPE_OVERRIDE_LOOKAHEAD", node.jjtGetValue(), node.jjtGetType());
		return null;
	}

	@Override
	public Object visit(SCOPE_OVERRIDE node, Object data) {
		debug("SCOPE_OVERRIDE", node.jjtGetValue(), node.jjtGetType());
		//return new Identifier (new TypeVar("typevar"),node.jjtGetValue().toString());
		return node.jjtGetValue().toString();
	}

	@Override
	public Object visit(QUALIFIED_ID node, Object data) {
		debug("QUALIFIED_ID", node.jjtGetValue(), node.jjtGetType());
		if (data.toString().compareTo("GET_SCOPE") == 0)
			return returnChild(JJTSCOPE_OVERRIDE, node, 0, data);
		return node.jjtGetValue();
	}

	@Override
	public Object visit(PTR_TO_MEMBER node, Object data) {
		debug("PTR_TO_MEMBER", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(QUALIFIED_TYPE node, Object data) {
		debug("QUALIFIED_TYPE", node.jjtGetValue(), node.jjtGetType());
		String cppClass = node.jjtGetChild(0).jjtGetValue().toString();
		String goolClass = RecognizerMatcher.matchClass(cppClass);
		if (goolClass != null) {
			if(!importCache.contains(goolClass)){
				stackClassActives.peek().addDependency(new RecognizedDependency(goolClass));
				importCache.add(goolClass);
			}
			return new TypeGoolLibraryClass(goolClass);
		}
		return new TypeClass(node.jjtGetChild(0).jjtGetValue().toString());
	}

	@Override
	public Object visit(TYPE_QUALIFIER node, Object data) {
		debug("TYPE_QUALIFIER", node.jjtGetValue(), node.jjtGetType());
		return convertModToGoolMod(node.jjtGetValue().toString());
	}

	@Override
	public Object visit(STORAGE_CLASS_SPECIFIER node, Object data) {
		debug("STORAGE_CLASS_SPECIFIER", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetValue() == null){
			return null;
		}
		return convertModToGoolMod(node.jjtGetValue().toString());
	}

	@Override
	public Object visit(BUILTIN_TYPE_SPECIFIER node, Object data) {
		debug("BUILTIN_TYPE_SPECIFIER", node.jjtGetValue(), node.jjtGetType());
		return convertIType(node.jjtGetValue().toString()); 
	}

	@Override
	public Object visit(INIT_DECLARATOR_LIST node, Object data) {
		debug("INIT_DECLARATOR_LIST", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(INIT_DECLARATOR node, Object data) {
		debug("INIT_DECLARATOR", node.jjtGetValue(), node.jjtGetType());
		if (data.toString().compareTo("GET_NAME") == 0 || data.toString().startsWith("GET_DIM")){
			return returnChild(JJTDECLARATOR, node, 0, data);
		}
		else if (data.toString().compareTo("GET_INIT") == 0){
			return returnChild(JJTINITIALIZER, node, 1, data);
		}
		return null;
	}

	@Override
	public Object visit(CLASS_HEAD node, Object data) {
		debug("CLASS_HEAD", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(CLASS_SPECIFIER node, Object data) {
		debug("CLASS_SPECIFIER", node.jjtGetValue(), node.jjtGetType());
		ClassDef cd = classExist(node.jjtGetValue().toString());

		if (cd == null){
			cd = new ClassDef(Modifier.PUBLIC, node.jjtGetValue().toString(), defaultPlatform);

			if (testChild((SimpleNode) node.jjtGetParent(), JJTSTORAGE_CLASS_SPECIFIER))
				cd.addModifier(Modifier.STATIC);

			stackClassActives.push(cd);
			if (testChild(node, JJTBASE_CLAUSE)){
				if (returnChild(JJTBASE_CLAUSE, node, 0, data) == null){return null;}
			}
			goolClasses.add(cd);
			node.childrenAccept(this, data);
			stackClassActives.pop();
			return RETURN_OK;
		}
		else {
			stackClassActives.push(cd);
			node.childrenAccept(this, data);
			stackClassActives.pop();
			return RETURN_OK;
		}
	}

	@Override
	public Object visit(BASE_CLAUSE node, Object data) {
		debug("BASE_CLAUSE", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() == 1){
			String name = ((SimpleNode) node.jjtGetChild(0)).jjtGetValue().toString();
			stackClassActives.peek().setParentClass(new TypeClass(name));
			return RETURN_OK;
		}
		else{
			return null;
		}
	}

	@Override
	public Object visit(BASE_SPECIFIER node, Object data) {
		debug("BASE_SPECIFIER", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(ACCESS_SPECIFIER node, Object data) {
		debug("ACCESS_SPECIFIER", node.jjtGetValue(), node.jjtGetType());
		return node.jjtGetValue().toString();
	}

	@Override
	public Object visit(MEMBER_DECLARATION node, Object data) {
		debug("MEMBER_DECLARATION", node.jjtGetValue(), node.jjtGetType());
		if (testChild(node, JJTACCESS_SPECIFIER)){
			accesModifierActive=convertModToGoolMod((String) returnChild(JJTACCESS_SPECIFIER, node, 0, data));
		}
		else if (testChild(node, JJTDTOR_DEFINITION)){
			if (visit((SimpleNode) node.jjtGetChild(0),data) == null){return null;}
		}
		else if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0).jjtGetId() == JJTDECLARATION_SPECIFIERS){

			Collection <Modifier> cm = (Collection <Modifier>) returnChild(JJTDECLARATION_SPECIFIERS,node,0,"GET_MODIFIERS");
			if (cm == null){cm = new ArrayList<Modifier>();}				
			IType type = (IType) returnChild(JJTDECLARATION_SPECIFIERS, node, 0, "GET_TYPE");
			if (type == null){return null;}
			if (testChild((SimpleNode) node.jjtGetChild(1), JJTPTR_OPERATOR,"*")){
				if (type.equals(TypeChar.INSTANCE))
					type=TypeString.INSTANCE;
				else
					return null;
			}

			SimpleNode nodeDecList = (SimpleNode) node.jjtGetChild(1); // <- MEMBER_DECLARATOR_LIST

			for (int i=0;i<nodeDecList.jjtGetNumChildren();i++){
				String name = (String) returnChild(JJTDECLARATOR, (SimpleNode) nodeDecList.jjtGetChild(i), 0, "GET_NAME");						
				if (name == null){return null;}

				cm.add(accesModifierActive);
				stackClassActives.peek().addField(new Field(cm, name, type, null));
			}
		}
		else if (testChild(node, JJTFUNCTION_DEFINITION)) {
			if (visit((SimpleNode) node.jjtGetChild(0),data) == null){return null;}
		}
		else {
			getUnrocognizedPart(((SimpleNode) node).jjtGetFirstToken(), ((SimpleNode) node).jjtGetLastToken());
			return null;
		}
		return RETURN_OK;

	}

	@Override
	public Object visit(MEMBER_DECLARATOR_LIST node, Object data) {
		debug("MEMBER_DECLARATOR_LIST", node.jjtGetValue(), node.jjtGetType());
		node.childrenAccept(this, data);
		return RETURN_OK;
	}

	@Override
	public Object visit(MEMBER_DECLARATOR node, Object data) {
		debug("MEMBER_DECLARATOR", node.jjtGetValue(), node.jjtGetType());
		return visit((SimpleNode) node.jjtGetChild(0),data);
	}

	@Override
	public Object visit(CONVERSION_FUNCTION_DECL_OR_DEF node, Object data) {
		debug("CONVERSION_FUNCTION_DECL_OR_DEF", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(ENUM_SPECIFIER node, Object data) {
		debug("ENUM_SPECIFIER", node.jjtGetValue(), node.jjtGetType());

		ClassDef cd = new ClassDef(Modifier.PUBLIC, node.jjtGetValue().toString(), defaultPlatform);
		cd.addMethod(new Constructor());
		cd.setIsEnum(true);
		stackClassActives.push(cd);
		goolClasses.add(cd);
		node.childrenAccept(this, node.jjtGetValue());
		stackClassActives.pop();
		return RETURN_OK;
	}

	@Override
	public Object visit(ENUMERATOR_LIST node, Object data) {
		debug("ENUMERATOR_LIST", node.jjtGetValue(), node.jjtGetType());
		for (int i=0;i<node.jjtGetNumChildren();i++){
			if (visit((SimpleNode) node.jjtGetChild(i), data) == null)
				getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
		}
		return RETURN_OK;
	}

	@Override
	public Object visit(ENUMERATOR node, Object data) {
		debug("ENUMERATOR", node.jjtGetValue(), node.jjtGetType());
		Field f = new Field(node.jjtGetValue().toString(),new TypeClass(data.toString()),new ClassNew(new TypeClass(data.toString())));
		f.addModifier(Modifier.STATIC);
		f.addModifier(Modifier.FINAL);
		stackClassActives.peek().addField(f);
		return RETURN_OK;
	}

	@Override
	public Object visit(PTR_OPERATOR node, Object data) {
		debug("PTR_OPERATOR", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(CV_QUALIFIER_SEQ node, Object data) {
		debug("CV_QUALIFIER_SEQ", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(DECLARATOR node, Object data) {
		debug("DECLARATOR", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetChild(0).jjtGetId() == JJTDIRECT_DECLARATOR){
			return returnChild(JJTDIRECT_DECLARATOR, node, 0, data);
		}
		else if (node.jjtGetChild(0).jjtGetId() == JJTPTR_OPERATOR && node.jjtGetChild(0).jjtGetValue() != null){
			return returnChild(JJTDECLARATOR, node, 1, data);
		}
		return null;
	}

	@Override
	public Object visit(DIRECT_DECLARATOR node, Object data) {
		debug("DIRECT_DECLARATOR", node.jjtGetValue(), node.jjtGetType());
		if (data.toString().startsWith("GET_DIM")){
			return returnChild(JJTDECLARATOR_SUFFIXES, node, 1, data);
		}
		if (node.jjtGetValue() != null){
			return visit((QUALIFIED_ID) node.jjtGetChild(0), data);
		}
		return null;
	}

	@Override
	public Object visit(DECLARATOR_SUFFIXES node, Object data) {
		debug("DECLARATOR_SUFFIXES", node.jjtGetValue(), node.jjtGetType());
		if (data.toString().compareTo("GET_DIM") == 0)
			return Integer.parseInt(node.jjtGetValue().toString());
		else{
			List<Expression> toReturn = new ArrayList<Expression>();
			for (int i=0;i<node.jjtGetNumChildren();i++){
				Expression e =(Expression) visit((SimpleNode) node.jjtGetChild(i), data);
				if (e == null){return null;}
				toReturn.add(e);
			}
			return toReturn;
		}
	}

	@Override
	public Object visit(FUNCTION_DECLARATOR_LOOKAHEAD node, Object data) {
		debug("FUNCTION_DECLARATOR_LOOKAHEAD", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(FUNCTION_DECLARATOR node, Object data) {
		debug("FUNCTION_DECLARATOR", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetChild(0).jjtGetId() == JJTFUNCTION_DIRECT_DECLARATOR){
			return returnChild(JJTFUNCTION_DIRECT_DECLARATOR, node, 0, data);
		}
		else if (node.jjtGetChild(0).jjtGetId() == JJTPTR_OPERATOR && node.jjtGetChild(0).jjtGetValue() != null){
			return returnChild(JJTFUNCTION_DECLARATOR, node, 1, data);
		}
		return null;
	}

	@Override
	public Object visit(FUNCTION_DIRECT_DECLARATOR node, Object data) {
		debug("FUNCTION_DIRECT_DECLARATOR", node.jjtGetValue(), node.jjtGetType());
		if (data.toString().compareTo("GET_EXCEP") == 0)
			return returnChild(JJTEXCEPTION_SPEC, node, 1, data);
		else if (data.toString().compareTo("GET_NAME") == 0 || data.toString().compareTo("GET_SCOPE") == 0)
			return returnChild(JJTQUALIFIED_ID, node, 0, data);
		else if (data.toString().compareTo("GET_PARAMS") == 0)
			return returnChild(JJTPARAMETER_LIST, node, 1, data);
		return null;
	}

	@Override
	public Object visit(DTOR_CTOR_DECL_SPEC node, Object data) {
		debug("DTOR_CTOR_DECL_SPEC", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(DTOR_DEFINITION node, Object data) {
		debug("DTOR_DEFINITION", node.jjtGetValue(), node.jjtGetType());
		Constructor c = new Constructor();
		if (testChild(node, JJTPARAMETER_LIST)){
			List<VarDeclaration> listVD = (List<VarDeclaration>) returnChild(JJTDTOR_DECLARATOR, node, 1, "GET_PARAMS");
			if (listVD == null){listVD=new ArrayList<VarDeclaration>();}
			for (VarDeclaration vd : listVD)
				c.addParameter(vd);
		}

		stackClassActives.peek().addMethod(c);
		methActive=c;
		visit((COMPOUND_STATEMENT) node.jjtGetChild(2), data);
		methActive=null;
		return RETURN_OK;
	}

	@Override
	public Object visit(CTOR_DEFINITION node, Object data) {
		debug("CTOR_DEFINITION", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(CTOR_DECLARATOR_LOOKAHEAD node, Object data) {
		debug("CTOR_DECLARATOR_LOOKAHEAD", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(CTOR_DECLARATOR node, Object data) {
		debug("CTOR_DECLARATOR", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(CTOR_INITIALIZER node, Object data) {
		debug("CTOR_INITIALIZER", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(SUPERCLASS_INIT node, Object data) {
		debug("SUPERCLASS_INIT", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(DTOR_DECLARATOR node, Object data) {
		debug("DTOR_DECLARATOR", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() > 1)
			return null;
		return visit((SimpleNode) node.jjtGetChild(0), data);
	}

	@Override
	public Object visit(SIMPLE_DTOR_DECLARATOR node, Object data) {
		debug("SIMPLE_DTOR_DECLARATOR", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() > 1)
			return null;
		return visit((SimpleNode) node.jjtGetChild(0), data);
	}

	@Override
	public Object visit(PARAMETER_LIST node, Object data) {
		debug("PARAMETER_LIST", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() == 0)
			getUnrocognizedPart(node.jjtGetFirstToken(), node.jjtGetLastToken());
		else {
			Object toReturn = returnChild(JJTPARAMETER_DECLARATION_LIST, node, 0, data);				
			if (node.jjtGetValue() != null)
				setUnrocognizedPart(", ...",node.jjtGetFirstToken());
			return toReturn;
		}
		return null;
	}

	@Override
	public Object visit(PARAMETER_DECLARATION_LIST node, Object data) {
		debug("PARAMETER_DECLARATION_LIST", node.jjtGetValue(), node.jjtGetType());
		List<VarDeclaration> toReturn = new ArrayList<VarDeclaration>();
		for (int i=0;i<node.jjtGetNumChildren();i++){
			VarDeclaration vd = (VarDeclaration) visit((SimpleNode) node.jjtGetChild(i), data);
			if (vd == null){getUnrocognizedPart(node.jjtGetFirstToken(), node.jjtGetLastToken());}
			else
				toReturn.add(vd);
		}
		return toReturn;
	}

	@Override
	public Object visit(PARAMETER_DECLARATION node, Object data) {
		debug("PARAMETER_DECLARATION", node.jjtGetValue(), node.jjtGetType());
		IType type = (IType) returnChild(JJTDECLARATION_SPECIFIERS, node, 0, data);
		if (type == null){return null;}
		if (testChild(node, JJTPTR_OPERATOR,"*")){
			if (type.equals(TypeChar.INSTANCE))
				type=TypeString.INSTANCE;
			else
				return null;
		}
		String name = (String) returnChild(JJTDECLARATOR, node, 1, "GET_NAME");						
		if (name == null){return null;}

		Collection <Modifier> cm = (Collection <Modifier>) visit((DECLARATION_SPECIFIERS) node.jjtGetChild(0), "GET_MODIFIERS");
		if (cm == null){return null;}

		VarDeclaration vd = new VarDeclaration(type, name);
		vd.setModifiers(cm);
		return vd;
	}

	@Override
	public Object visit(INITIALIZER node, Object data) {
		debug("INITIALIZER", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetValue() == null)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	// TODO : ajouter les cast sur les types tableaux
	public Object visit(TYPE_NAME node, Object data) {
		debug("TYPE_NAME", node.jjtGetValue(), node.jjtGetType());
		System.out.println(node.jjtGetChild(1).jjtGetValue());
		if(((String)node.jjtGetChild(1).jjtGetValue()).compareTo("[]")==0){
			if (node.jjtGetChild(1).jjtGetNumChildren() > 0){
				IType type = (IType) returnChild(JJTDECLARATION_SPECIFIERS, node, 0, data);
				//Expression exp=(Expression)visit((SimpleNode) node.jjtGetChild(1).jjtGetChild(0), data);
				//String s=(String)visit((SimpleNode) node.jjtGetChild(0), data);
				return new TypeArray(type);
			}
			else{
				IType type = (IType) returnChild(JJTDECLARATION_SPECIFIERS, node, 0, data);
				return new TypeArray(type);
			}
		}
		else return visit((SimpleNode) node.jjtGetChild(0), data);
	}

	@Override
	public Object visit(ABSTRACT_DECLARATOR node, Object data) {
		debug("ABSTRACT_DECLARATOR", node.jjtGetValue(), node.jjtGetType());

		return visit((SimpleNode) node.jjtGetChild(0), data);
	}

	@Override
	public Object visit(ABSTRACT_DECLARATOR_SUFFIX node, Object data) {
		debug("ABSTRACT_DECLARATOR_SUFFIX", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(TEMPLATE_HEAD node, Object data) {
		debug("TEMPLATE_HEAD", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(TEMPLATE_PARAMETER_LIST node, Object data) {
		debug("TEMPLATE_PARAMETER_LIST", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(TEMPLATE_PARAMETER node, Object data) {
		debug("TEMPLATE_PARAMETER", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(TEMPLATE_ID node, Object data) {
		debug("TEMPLATE_ID", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(TEMPLATE_ARGUMENT_LIST node, Object data) {
		debug("TEMPLATE_ARGUMENT_LIST", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(TEMPLATE_ARGUMENT node, Object data) {
		debug("TEMPLATE_ARGUMENT", node.jjtGetValue(), node.jjtGetType());

		return null;
	}

	@Override
	public Object visit(STATEMENT_LIST node, Object data) {
		debug("STATEMENT_LIST", node.jjtGetValue(), node.jjtGetType());
		Block b = new Block();
		for (int i=0;i<node.jjtGetNumChildren();i++){
			Statement s = (Statement) visit((SimpleNode) node.jjtGetChild(i) , data);
			if (s == null)
				getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
			else
				b.addStatement(s);
		}
		return b;
	}

	@Override
	public Object visit(STATEMENT node, Object data) {
		debug("STATEMENT", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() > 1)
			return null;
		return visit((SimpleNode) node.jjtGetChild(0), data);
	}

	@Override
	public Object visit(LABELED_STATEMENT node, Object data) {
		debug("LABELED_STATEMENT", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetValue() != null){
			Expression expCase = (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
			Block stmtCase = (Block) visit((SimpleNode) node.jjtGetChild(1),data);
			if (expCase == null || stmtCase == null){return null;}
			return new Case(expCase,stmtCase);
		}
		return null;
	}

	@Override
	public Object visit(COMPOUND_STATEMENT node, Object data) {
		debug("COMPOUND_STATEMENT", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() > 0)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return new Block();
	}

	@Override
	public Object visit(SELECTION_STATEMENT node, Object data) {
		debug("SELECTION_STATEMENT", node.jjtGetValue(), node.jjtGetType());
		if (((String) node.jjtGetValue()).compareTo("if") == 0){
			Expression cond = (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
			if (cond == null){return null;}
			Statement thenSt = (Statement) visit((SimpleNode) node.jjtGetChild(1),data);
			if (thenSt == null){thenSt=new Block();}

			Statement elseSt;
			if (node.jjtGetNumChildren() == 3){
				elseSt = (Statement) visit((SimpleNode) node.jjtGetChild(2),data);
				if (elseSt == null){elseSt=new Block();}
			}
			else
				elseSt=null;			
			return new If (cond,thenSt,elseSt);
		}
		// Cas du switch
		else if (((String) node.jjtGetValue()).compareTo("switch") == 0){
			List <Case> l=new ArrayList<Case>();
			Expression cond = (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
			if (cond == null){return null;}

			Block listCase = (Block) returnChild(JJTSTATEMENT, node, 1, data);

			for (int i=0; i<listCase.getStatements().size();i++){
				l.add((Case) listCase.getStatements().get(i));
			}
			return new Switch(cond, l);
		}
		else if (node.jjtGetNumChildren() > 0)
			return visit((SimpleNode) node.jjtGetChild(0),data);
		return null;
	}

	@Override
	public Object visit(ITERATION_STATEMENT node, Object data) {
		debug("ITERATION_STATEMENT", node.jjtGetValue(), node.jjtGetType());
		if (((String) node.jjtGetValue()).compareTo("while")== 0){
			Expression condWhile = (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
			if (condWhile == null){return null;}
			Statement stWhile = (Statement) visit((SimpleNode) node.jjtGetChild(1),data);
			if (stWhile == null){stWhile=new Block();}
			return new While (condWhile,stWhile);
		}

		else if (((String) node.jjtGetValue()).startsWith("for")){

			if (node.jjtGetValue().toString().split(" ").length == 1){
				Statement stFor = (Statement) visit((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1),data);
				if (stFor == null)
					getUnrocognizedPart(((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1)).jjtGetLastToken());
				return new For(null,null,null,stFor);
			}
			else {				
				String pattern = node.jjtGetValue().toString().split(" ")[1];
				Statement stFor=null;
				Statement initFor=null;
				Expression condFor=null;
				Statement updater = null;

				for (int i=0;i<node.jjtGetNumChildren()-1;i++){
					if (pattern.charAt(i) == '1'){
						initFor = (Statement) visit((SimpleNode) node.jjtGetChild(i),data);
						if (initFor == null)
							getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
					}
					else if (pattern.charAt(i) == '2'){
						condFor = (Expression) visit((SimpleNode) node.jjtGetChild(i),data);
						if (condFor == null)
							getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
					}
					else{
						updater = (Statement) visit((SimpleNode) node.jjtGetChild(i),data);
						if (updater == null)
							getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
					}
				}
				stFor = (Statement) visit((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1),data);
				if (stFor == null)
					getUnrocognizedPart(((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(node.jjtGetNumChildren()-1)).jjtGetLastToken());

				return new For(initFor,condFor,updater,stFor);
			}
		}
		else if (((String) node.jjtGetValue()).compareTo("dowhile")== 0){
			Expression condDo = (Expression) visit((SimpleNode) node.jjtGetChild(1),data);
			if (condDo == null){return null;}		
			Statement stDo = (Statement) visit((SimpleNode) node.jjtGetChild(0),data);
			if (stDo == null){return null;}				
			return new DoWhile(stDo,condDo);
		}
		else
			return null;
	}

	@Override
	public Object visit(JUMP_STATEMENT node, Object data) {
		debug("JUMP_STATEMENT", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetValue() != null){
			Expression tr = (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
			if (tr == null){return null;}
			return new Return (tr);
		}
		return null;
	}

	@Override
	public Object visit(TRY_BLOCK node, Object data) {
		debug("TRY_BLOCK", node.jjtGetValue(), node.jjtGetType());
		Block block = (Block) visit((SimpleNode) node.jjtGetChild(0),data);
		if (block == null){return null;}
		Block finallyBlock = new Block();
		List<Catch> catches = new ArrayList<Catch>();

		for (int i=1;i<node.jjtGetNumChildren();i++){
			if (((String) ((SimpleNode) node.jjtGetChild(i)).jjtGetValue()).compareTo("finally") == 0){
				finallyBlock = (Block) visit((SimpleNode) node.jjtGetChild(i),data);
				if (finallyBlock == null){return null;}
			}
			else{
				Catch c = (Catch) visit((SimpleNode) node.jjtGetChild(i),data);
				if (c == null){
					getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
				}
				else
					catches.add(c);
			}
		}			
		return new Try(catches, block, finallyBlock);
	}

	@Override
	public Object visit(HANDLER node, Object data) {
		debug("HANDLER", node.jjtGetValue(), node.jjtGetType());
		if (((String) node.jjtGetValue()).compareTo("catch") == 0){
			List<VarDeclaration> listVD = (List<VarDeclaration>) visit((SimpleNode) node.jjtGetChild(0),data);
			if (listVD == null || listVD.size() != 1){return null;}
			Block block = (Block) visit((SimpleNode) node.jjtGetChild(1),data);
			if (block == null){return null;}
			return new Catch(listVD.get(0), block);
		}
		else{
			return (Block) visit((SimpleNode) node.jjtGetChild(0),data);
		}
	}

	@Override
	public Object visit(EXCEPTION_DECLARATION node, Object data) {
		debug("EXCEPTION_DECLARATION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() > 0)
			return visit((SimpleNode) node.jjtGetChild(0),data);
		return null;
	}

	@Override
	public Object visit(THROW_STATEMENT node, Object data) {
		debug("THROW_STATEMENT", node.jjtGetValue(), node.jjtGetType());
		return null;
	}

	@Override
	public Object visit(EXPRESSION node, Object data) {
		debug("EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(ASSIGNMENT_EXPRESSION node, Object data) {
		debug("ASSIGNMENT_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren()>1){

			Node varAss = (Node) visit((SimpleNode) node.jjtGetChild(0),data);
			Expression expAss = (Expression) visit((SimpleNode) node.jjtGetChild(1),data);
			if (((String) node.jjtGetValue()).compareTo("=")== 0){
				if (varAss == null || expAss == null){return null;}
				return new Assign(varAss, expAss);
			}
			else if (((String) node.jjtGetValue()).compareTo("+=")== 0){
				Operator operator = Operator.PLUS;
				String textualoperator = "+";
				if (varAss == null || expAss == null){return null;}
				return new CompoundAssign(varAss, expAss, operator, textualoperator, TypeInt.INSTANCE);
			}
			else if (((String) node.jjtGetValue()).compareTo("-=")== 0){

				Operator operator = Operator.MINUS;
				String textualoperator = "-";
				if (varAss == null || expAss == null){return null;}
				return new CompoundAssign(varAss, expAss, operator, textualoperator, TypeInt.INSTANCE);
			}
			else if (((String) node.jjtGetValue()).compareTo("*=")== 0){

				Operator operator = Operator.MULT;
				String textualoperator = "*";
				if (varAss == null || expAss == null){return null;}
				return new CompoundAssign(varAss, expAss, operator, textualoperator, TypeInt.INSTANCE);
			}
			else if (((String) node.jjtGetValue()).compareTo("/=")== 0){

				Operator operator = Operator.NOT;
				String textualoperator = "";
				if (varAss == null || expAss == null){return null;}
				return new CompoundAssign(varAss, expAss, operator, textualoperator, TypeInt.INSTANCE);
			}
			else {return null;}
		}
		else if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}


	@Override
	public Object visit(CONDITIONAL_EXPRESSION node, Object data) {
		debug("CONDITIONAL_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(CONSTANT_EXPRESSION node, Object data) {
		debug("CONSTANT_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(LOGICAL_OR_EXPRESSION node, Object data) {
		debug("LOGICAL_OR_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		return getBooleanExpression(node,data);
	}

	@Override
	public Object visit(LOGICAL_AND_EXPRESSION node, Object data) {
		debug("LOGICAL_AND_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		return getBooleanExpression(node,data);
	}

	@Override
	public Object visit(INCLUSIVE_OR_EXPRESSION node, Object data) {
		debug("INCLUSIVE_OR_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(EXCLUSIVE_OR_EXPRESSION node, Object data) {
		debug("EXCLUSIVE_OR_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(AND_EXPRESSION node, Object data) {
		debug("AND_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(EQUALITY_EXPRESSION node, Object data) {
		debug("EQUALITY_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		return getBooleanExpression(node,data);
	}

	@Override
	public Object visit(RELATIONAL_EXPRESSION node, Object data) {
		debug("RELATIONAL_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		return getBooleanExpression(node,data);
	}

	@Override
	public Object visit(SHIFT_EXPRESSION node, Object data) {
		debug("SHIFT_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (isFunctionPrint(node,data)){							
			stackClassActives.peek().addDependency(new SystemOutDependency());
			GoolCall gc = new SystemOutPrintCall();
			gc.addParameter(getExpressionPrint(node, 1, data));
			return gc;
		}
		else if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(ADDITIVE_EXPRESSION node, Object data) {
		debug("ADDITIVE_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		return getBinaryExpression (node, node.jjtGetNumChildren()-1, (List<?>) node.jjtGetValue(), data);
	}

	@Override
	public Object visit(MULTIPLICATIVE_EXPRESSION node, Object data) {
		debug("MULTIPLICATIVE_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		return getBinaryExpression (node, node.jjtGetNumChildren()-1, (List<?>) node.jjtGetValue(), data);
	}

	@Override
	public Object visit(PM_EXPRESSION node, Object data) {
		debug("PM_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(CAST_EXPRESSION node, Object data) {
		debug("CAST_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() > 1){
			Expression exp = (Expression) visit((SimpleNode) node.jjtGetChild(1),data);
			if (exp == null){return null;}
			IType type = (IType) returnChild(JJTTYPE_NAME, node, 0, data);
			if (type == null){return null;}
			if (testChild(node, JJTPTR_OPERATOR,"*")){
				if (type.equals(TypeChar.INSTANCE))
					type=TypeString.INSTANCE;
				else
					return null;
			}				
			return new CastExpression(type, exp);
		}
		else if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(UNARY_EXPRESSION node, Object data) {
		debug("UNARY_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetChild(0).jjtGetId() == JJTTYPE_NAME)
			return null;
		else if (!data.toString().startsWith("GET_") && Integer.parseInt(data.toString()) > 1){
			return null;
		}
		else if (node.jjtGetChild(0).jjtGetId() == JJTUNARY_OPERATOR && node.jjtGetChild(0).jjtGetValue() != null && node.jjtGetChild(0).jjtGetValue().toString().compareTo("!") == 0){
			Expression expr = (Expression) visit((SimpleNode) node.jjtGetChild(1),data);
			if (expr == null){return null;}
			return new UnaryOperation(convertSymToOpe("!"), expr, TypeBool.INSTANCE, "!");
		}
		else if (node.jjtGetChild(0).jjtGetId() == JJTUNARY_OPERATOR && node.jjtGetChild(0).jjtGetValue() != null && node.jjtGetChild(0).jjtGetValue().toString().compareTo("*") == 0){
			Expression expr = (Expression) visit((SimpleNode) node.jjtGetChild(1),data);
			if (expr == null){return null;}
			if (expr instanceof This)
				return expr;
			else
				return null;
		}
		else if (node.jjtGetValue()!=null && ((String) node.jjtGetValue()).compareTo("++")== 0){
			int nData = 0;
			if (data != null)
				nData+=Integer.parseInt(data.toString())+1;

			Operator operator = Operator.PREFIX_INCREMENT;
			Expression varPost= (Expression) visit((SimpleNode) node.jjtGetChild(0),nData);
			if (varPost == null){return null;}
			return new UnaryOperation(operator,varPost, TypeInt.INSTANCE, "++");

		} else if (node.jjtGetValue()!=null && ((String) node.jjtGetValue()).compareTo("--")== 0){
			int nData = 0;
			if (data != null)
				nData+=Integer.parseInt(data.toString())+1;

			Operator operator = Operator.PREFIX_DECREMENT;
			Expression varPost= (Expression) visit((SimpleNode) node.jjtGetChild(0),nData);
			if (varPost == null){return null;}
			return new UnaryOperation(operator,varPost, TypeInt.INSTANCE, "--");
		}
		else if (node.jjtGetNumChildren() == 1 && node.jjtGetValue() != null)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(NEW_EXPRESSION node, Object data) {
		debug("NEW_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		IType type = (TypeClass) visit((SimpleNode) node.jjtGetChild(0).jjtGetChild(0), data);
		if (type == null){return null;}		
		ClassNew cn = new ClassNew(type);
		List <Expression> listExpr = (List <Expression>) visit((SimpleNode) node.jjtGetChild(1).jjtGetChild(0),data);
		if (listExpr == null){return null;}
		cn.addParameters(listExpr);
		return cn;
	}

	@Override
	public Object visit(NEW_TYPE_ID node, Object data) {
		debug("NEW_TYPE_ID", node.jjtGetValue(), node.jjtGetType());
		return null;
	}

	@Override
	public Object visit(NEW_DECLARATOR node, Object data) {
		debug("NEW_DECLARATOR", node.jjtGetValue(), node.jjtGetType());
		return null;
	}

	@Override
	public Object visit(DIRECT_NEW_DECLARATOR node, Object data) {
		debug("DIRECT_NEW_DECLARATOR", node.jjtGetValue(), node.jjtGetType());
		return null;
	}

	@Override
	public Object visit(NEW_INITIALIZER node, Object data) {
		debug("NEW_INITIALIZER", node.jjtGetValue(), node.jjtGetType());
		return null;
	}

	@Override
	public Object visit(DELETE_EXPRESSION node, Object data) {
		debug("DELETE_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		return null;
	}

	@Override
	public Object visit(UNARY_OPERATOR node, Object data) {
		debug("UNARY_OPERATOR", node.jjtGetValue(), node.jjtGetType());
		return null;
	}

	private Expression getLinkedTarget (SimpleNode node, int i, List<String> l, Object data){
		if (node.jjtGetChild(i).jjtGetId() == JJTEXPRESSION_LIST){
			return getLinkedTarget(node, i-1, l, data);
		}
		else if (i != 0){
			if (l.get(i).compareTo("()") == 0){
				Identifier id = (Identifier) returnChild(JJTID_EXPRESSION, node, i, data);
				Expression target = getLinkedTarget(node, i-1, l, data);
				if (id == null || target == null){return null;}
				MethCall m = new MethCall(new TypeMethod("typemeth"), new MemberSelect(target, new VarDeclaration(id.getType(), id.getName())));
				if (i+1 < node.jjtGetNumChildren() && node.jjtGetChild(i+1).jjtGetId() == JJTEXPRESSION_LIST){
					m.addParameters((List<Expression>) visit((SimpleNode) node.jjtGetChild(i+1),data));
				}
				return m;
			}
			else{
				Identifier id = (Identifier) returnChild(JJTID_EXPRESSION, node, i, data);
				Expression target = getLinkedTarget(node, i-1, l, data);
				if (id == null || target == null){return null;}
				return new MemberSelect(target, new VarDeclaration(id.getType(), id.getName()));
			}
		}
		else if (l.get(i).compareTo("()") == 0){
			Identifier id = (Identifier) returnChild(JJTPRIMARY_EXPRESSION, node, 0, data);
			if (id == null){return null;}
			MethCall m = new MethCall(new TypeMethod("typemeth"), id);
			if (i+1 < node.jjtGetNumChildren() && node.jjtGetChild(i+1).jjtGetId() == JJTEXPRESSION_LIST){
				m.addParameters((List<Expression>) visit((SimpleNode) node.jjtGetChild(i+1),data));
			}
			return m;
		}
		else if (l.get(i).compareTo("i") == 0){
			return (Identifier) returnChild(JJTPRIMARY_EXPRESSION, node, 0, data);
		}
		else
			return null;
	}

	@Override
	public Object visit(POSTFIX_EXPRESSION node, Object data) {
		debug("POSTFIX_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetValue() instanceof List<?>){
			List<String> l = (List<String>) node.jjtGetValue();			
			return getLinkedTarget(node, node.jjtGetNumChildren()-1, l, data);
		}
		else if (node.jjtGetValue() != null && ((String) node.jjtGetValue()).compareTo("()") == 0){
			Identifier name = (Identifier) returnChild(JJTPRIMARY_EXPRESSION, node, 0, "GET_ID_FCT");						
			MethCall m = new MethCall(new TypeMethod("typemeth"), name);
			if (node.jjtGetNumChildren() > 1){
				m.addParameters((List<Expression>) visit((SimpleNode) node.jjtGetChild(1),data));
			}
			return m;
		}
		else if (node.jjtGetValue() != null && ((String) node.jjtGetValue()).compareTo("[]") == 0){
			return getListDim (node,node.jjtGetNumChildren()-1,data);
		}
		else if (node.jjtGetValue()!=null && ((String) node.jjtGetValue()).compareTo("++")== 0){
			Operator operator = Operator.POSTFIX_INCREMENT;
			Expression varPost= (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
			if (varPost == null){return null;}
			return new UnaryOperation(operator,varPost, TypeInt.INSTANCE, (String) node.jjtGetValue());

		} else if (node.jjtGetValue()!=null && ((String) node.jjtGetValue()).compareTo("--")== 0){
			Operator operator = Operator.POSTFIX_DECREMENT;
			Expression varPost= (Expression) visit((SimpleNode) node.jjtGetChild(0),data);
			if (varPost == null){return null;}
			return new UnaryOperation(operator,varPost, TypeInt.INSTANCE, (String) node.jjtGetValue());
		}
		else if (node.jjtGetValue()!=null && ((String) node.jjtGetValue()).compareTo("ERROR")== 0)
			return null;
		else if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(ID_EXPRESSION node, Object data) {
		debug("ID_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (data.toString().compareTo("GET_ID_FCT") == 0 && node.jjtGetNumChildren() > 0){
			String id = (String) visit((SimpleNode) node.jjtGetChild(0),data);
			if (id == null){return null;}
			return new Identifier (new TypeVar("typevar"),id.replaceAll("::",".")+node.jjtGetValue().toString());
		}
		else if (node.jjtGetNumChildren() > 0){
			String id = (String) visit((SimpleNode) node.jjtGetChild(0),data);
			if (id == null){return null;}
			Identifier idf = new Identifier (new TypeVar("typevar"),id.replaceAll("::",".").substring(0, id.replaceAll("::",".").length()-1));
			return new FieldAccess(new TypeVar("typevar"), idf,node.jjtGetValue().toString());		}
		else
			return new Identifier (new TypeVar("typevar"),node.jjtGetValue().toString());
	}

	@Override
	public Object visit(PRIMARY_EXPRESSION node, Object data) {
		debug("PRIMARY_EXPRESSION", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetValue() != null && node.jjtGetValue().toString().compareTo("this") == 0)
			return new This(stackClassActives.peek().getType());
		else if (node.jjtGetNumChildren() == 0)
			return new Constant(TypeString.INSTANCE,((String) node.jjtGetValue()).subSequence(1, ((String) node.jjtGetValue()).length()-1));
		else if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(EXPRESSION_LIST node, Object data) {
		debug("EXPRESSION_LIST", node.jjtGetValue(), node.jjtGetType());
		List <Expression> listExp = new ArrayList<Expression>();
		for (int i=0;i<node.jjtGetNumChildren();i++){
			Expression e = (Expression) visit((SimpleNode) node.jjtGetChild(i),data);
			if (e == null)
				getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
			else
				listExp.add(e);
		}
		return listExp;
	}

	@Override
	public Object visit(CONSTANT node, Object data) {
		debug("CONSTANT", node.jjtGetValue(), node.jjtGetType());
		if (((String) node.jjtGetValue()).startsWith("'") || ((String) node.jjtGetValue()).endsWith("'"))
			return new Constant(convertIType((String) node.jjtGetType()),((String) node.jjtGetValue()).subSequence(1, ((String) node.jjtGetValue()).length()-1));
		else
			return new Constant(convertIType((String) node.jjtGetType()),node.jjtGetValue());
	}

	@Override
	public Object visit(OPTOR node, Object data) {
		debug("OPTOR", node.jjtGetValue(), node.jjtGetType());
		return null;
	}

	@Override
	public Object visit(EXCEPTION_SPEC node, Object data) {
		debug("EXCEPTION_SPEC", node.jjtGetValue(), node.jjtGetType());
		if (node.jjtGetNumChildren() == 1)
			return visit((SimpleNode) node.jjtGetChild(0), data);
		return null;
	}

	@Override
	public Object visit(EXCEPTION_LIST node, Object data) {
		debug("EXCEPTION_LIST", node.jjtGetValue(), node.jjtGetType());
		List<IType> listType = new ArrayList<IType>();
		for (int i=0;i<node.jjtGetNumChildren();i++){
			IType type = (IType) returnChild(JJTTYPE_NAME, node, 0, data);
			if (type == null){
				getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
			}
			else if (testChild(node, JJTPTR_OPERATOR,"*")){
				if (type.equals(TypeChar.INSTANCE))
					type=TypeString.INSTANCE;
				else
					getUnrocognizedPart(((SimpleNode) node.jjtGetChild(i)).jjtGetFirstToken(), ((SimpleNode) node.jjtGetChild(i)).jjtGetLastToken());
			}
			else
				listType.add(type);
		}
		return listType;
	}

	@Override
	public Object visit(INCLUDE_SPECIFER node, Object data) {
		debug("INCLUDE_SPECIFER", node.jjtGetValue(), node.jjtGetType());
		// TODO: faire le chainage et ajouter le noeud dependancy
		//System.out.println("[CppRecognizer] BEGIN of visitINCLUDE_SPECIFER.");
		// The destination package is either null or that specified by the
		// visited package
		List<Dependency> dependencies = new ArrayList<Dependency>();

		// GoolMatcher init call
		// GoolMatcher init call

		String dependencyString = ((String)node.jjtGetValue()).substring(1,((String)node.jjtGetValue()).length()-1);
		if (!RecognizerMatcher.matchImport(dependencyString )) {
			dependencies.add(new UnrecognizedDependency(dependencyString));
		}
		else{
			//dependencies.add(new RecognizedDependency(RecognizerMatcher.matchClass("GoolFileImpl")));

			//dependencies.add(new RecognizedDependency("io.GoolFile"/* Modifier car normalement on doit avoir la correspondance dans les fichier*/));
		}
		//System.out.println(RecognizerMatcher.matchClass("GoolFileImpl"));


		/*for (ClassDef classDef : getGoolClasses()) {
			GoolLibraryClassAstBuilder.init(defaultPlatform);
			int x = 0;
			for (Dependency dep : classDef.getDependencies()) {
				x++;
				if (dep instanceof RecognizedDependency) {
					GoolLibraryClassAstBuilder
					.buildGoolClass(((RecognizedDependency) dep)
							.getName());
				}
			}
		}*/
		//RecognizerMatcher.printMatchTables();
		stackClassActives.peek().addDependencies(dependencies);

		//System.out.println("[CppRecognizer] END of visitINCLUDE_SPECIFER.");

		return null;
	}


}
