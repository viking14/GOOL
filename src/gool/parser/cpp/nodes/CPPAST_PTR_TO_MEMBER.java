/* Generated By:JJTree: Do not edit this line. CPPAST_PTR_TO_MEMBER.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_PTR_TO_MEMBER extends SimpleNode {
  public CPPAST_PTR_TO_MEMBER(int id) {
    super(id);
  }

  public CPPAST_PTR_TO_MEMBER(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=0206515dcee8c62dbdde97cf41e6a2f9 (do not edit this line) */
