/* Generated By:JJTree: Do not edit this line. EXCEPTION_SPEC.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class EXCEPTION_SPEC extends SimpleNode {
  public EXCEPTION_SPEC(int id) {
    super(id);
  }

  public EXCEPTION_SPEC(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d7c732c8bccb1404190c415f43d5c833 (do not edit this line) */
