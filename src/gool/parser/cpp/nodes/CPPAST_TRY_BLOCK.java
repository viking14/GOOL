/* Generated By:JJTree: Do not edit this line. CPPAST_TRY_BLOCK.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_TRY_BLOCK extends SimpleNode {
  public CPPAST_TRY_BLOCK(int id) {
    super(id);
  }

  public CPPAST_TRY_BLOCK(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=95bb57c6b44c26a16b4ebb443a0d7d9a (do not edit this line) */
