/* Generated By:JJTree: Do not edit this line. TEMPLATE_ID.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class TEMPLATE_ID extends SimpleNode {
  public TEMPLATE_ID(int id) {
    super(id);
  }

  public TEMPLATE_ID(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e6d4605779f7a0e9b15634ae0446463c (do not edit this line) */
