/* Generated By:JJTree: Do not edit this line. LABELED_STATEMENT.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class LABELED_STATEMENT extends SimpleNode {
  public LABELED_STATEMENT(int id) {
    super(id);
  }

  public LABELED_STATEMENT(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=10c0ef1c0ed058ca76cc824467205f9d (do not edit this line) */