/* Generated By:JJTree: Do not edit this line. CPPAST_DECLARATION_SPECIFIERS.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_DECLARATION_SPECIFIERS extends SimpleNode {
  public CPPAST_DECLARATION_SPECIFIERS(int id) {
    super(id);
  }

  public CPPAST_DECLARATION_SPECIFIERS(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=a32503eee100aafebe63c2b61298fde5 (do not edit this line) */
