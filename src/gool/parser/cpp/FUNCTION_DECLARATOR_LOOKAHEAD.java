/* Generated By:JJTree: Do not edit this line. FUNCTION_DECLARATOR_LOOKAHEAD.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class FUNCTION_DECLARATOR_LOOKAHEAD extends SimpleNode {
  public FUNCTION_DECLARATOR_LOOKAHEAD(int id) {
    super(id);
  }

  public FUNCTION_DECLARATOR_LOOKAHEAD(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=5f339712075c1d39add4e4d50b4d7a46 (do not edit this line) */