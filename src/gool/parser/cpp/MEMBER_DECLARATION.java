/* Generated By:JJTree: Do not edit this line. MEMBER_DECLARATION.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class MEMBER_DECLARATION extends SimpleNode {
  public MEMBER_DECLARATION(int id) {
    super(id);
  }

  public MEMBER_DECLARATION(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e7f61275a224df355f984198273a1306 (do not edit this line) */