/* Generated By:JJTree: Do not edit this line. CPPAST_QUALIFIED_TYPE.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_QUALIFIED_TYPE extends SimpleNode {
  public CPPAST_QUALIFIED_TYPE(int id) {
    super(id);
  }

  public CPPAST_QUALIFIED_TYPE(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=4e35d40957cb9b15341b3c4438d96f69 (do not edit this line) */
