/* Generated By:JJTree: Do not edit this line. CPPAST_NEW_TYPE_ID.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_NEW_TYPE_ID extends SimpleNode {
  public CPPAST_NEW_TYPE_ID(int id) {
    super(id);
  }

  public CPPAST_NEW_TYPE_ID(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=2c9d0b17de56b7502a2d684705ae972d (do not edit this line) */
