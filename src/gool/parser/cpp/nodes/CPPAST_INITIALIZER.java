/* Generated By:JJTree: Do not edit this line. CPPAST_INITIALIZER.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_INITIALIZER extends SimpleNode {
  public CPPAST_INITIALIZER(int id) {
    super(id);
  }

  public CPPAST_INITIALIZER(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=ac60edb201434308f1ed1820cdc3a074 (do not edit this line) */