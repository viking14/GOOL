/* Generated By:JJTree: Do not edit this line. BASE_CLAUSE.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class BASE_CLAUSE extends SimpleNode {
  public BASE_CLAUSE(int id) {
    super(id);
  }

  public BASE_CLAUSE(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=87c4192846405fcb408236e0eb189048 (do not edit this line) */
