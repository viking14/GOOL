/* Generated By:JJTree: Do not edit this line. CONSTANT_EXPRESSION.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class CONSTANT_EXPRESSION extends SimpleNode {
  public CONSTANT_EXPRESSION(int id) {
    super(id);
  }

  public CONSTANT_EXPRESSION(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=cefc917fe5e5a4a76d5fc5368e68c150 (do not edit this line) */
