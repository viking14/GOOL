/* Generated By:JJTree: Do not edit this line. CPPAST_DIRECT_NEW_DECLARATOR.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_DIRECT_NEW_DECLARATOR extends SimpleNode {
  public CPPAST_DIRECT_NEW_DECLARATOR(int id) {
    super(id);
  }

  public CPPAST_DIRECT_NEW_DECLARATOR(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=caa442ffcd361c485063617c13f531ab (do not edit this line) */