/* Generated By:JJTree: Do not edit this line. SELECTION_STATEMENT.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class SELECTION_STATEMENT extends SimpleNode {
  public SELECTION_STATEMENT(int id) {
    super(id);
  }

  public SELECTION_STATEMENT(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=61476c47097a5ce13e5f606c665ea878 (do not edit this line) */
