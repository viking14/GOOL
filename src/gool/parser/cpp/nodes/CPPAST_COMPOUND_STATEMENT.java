/* Generated By:JJTree: Do not edit this line. CPPAST_COMPOUND_STATEMENT.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_COMPOUND_STATEMENT extends SimpleNode {
  public CPPAST_COMPOUND_STATEMENT(int id) {
    super(id);
  }

  public CPPAST_COMPOUND_STATEMENT(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d8708ea3a9c81a024c7b7983e532965f (do not edit this line) */
