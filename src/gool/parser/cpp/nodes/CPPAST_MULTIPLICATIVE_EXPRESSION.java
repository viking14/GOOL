/* Generated By:JJTree: Do not edit this line. CPPAST_MULTIPLICATIVE_EXPRESSION.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_MULTIPLICATIVE_EXPRESSION extends SimpleNode {
  public CPPAST_MULTIPLICATIVE_EXPRESSION(int id) {
    super(id);
  }

  public CPPAST_MULTIPLICATIVE_EXPRESSION(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=2bc9f13940a89b23beda84d1d61cae05 (do not edit this line) */