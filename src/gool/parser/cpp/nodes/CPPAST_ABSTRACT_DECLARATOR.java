/* Generated By:JJTree: Do not edit this line. CPPAST_ABSTRACT_DECLARATOR.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_ABSTRACT_DECLARATOR extends SimpleNode {
  public CPPAST_ABSTRACT_DECLARATOR(int id) {
    super(id);
  }

  public CPPAST_ABSTRACT_DECLARATOR(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=3c72b5eebfcddf26d5a76caada541a54 (do not edit this line) */
