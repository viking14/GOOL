/* Generated By:JJTree: Do not edit this line. CPPAST_NEW_DECLARATOR.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_NEW_DECLARATOR extends SimpleNode {
  public CPPAST_NEW_DECLARATOR(int id) {
    super(id);
  }

  public CPPAST_NEW_DECLARATOR(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=beb20ba05dbfc1dac6d2fc8d98b5ac41 (do not edit this line) */
