/* Generated By:JJTree: Do not edit this line. UNARY_EXPRESSION.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class UNARY_EXPRESSION extends SimpleNode {
  public UNARY_EXPRESSION(int id) {
    super(id);
  }

  public UNARY_EXPRESSION(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=6a32d8c75236f935b4950f56a3bd66ae (do not edit this line) */