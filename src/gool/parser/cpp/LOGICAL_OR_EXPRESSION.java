/* Generated By:JJTree: Do not edit this line. LOGICAL_OR_EXPRESSION.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class LOGICAL_OR_EXPRESSION extends SimpleNode {
  public LOGICAL_OR_EXPRESSION(int id) {
    super(id);
  }

  public LOGICAL_OR_EXPRESSION(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=7fa8d4db70e94a9869cf40063f436241 (do not edit this line) */
