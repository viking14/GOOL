/* Generated By:JJTree: Do not edit this line. CPPAST_EXPRESSION.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_EXPRESSION extends SimpleNode {
  public CPPAST_EXPRESSION(int id) {
    super(id);
  }

  public CPPAST_EXPRESSION(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f23223c98ae02c06b88a9f6d16a130a9 (do not edit this line) */
