/* Generated By:JJTree: Do not edit this line. CPPAST_CV_QUALIFIER_SEQ.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=CPPAST_,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp.nodes;

import gool.parser.cpp.*;

public
class CPPAST_CV_QUALIFIER_SEQ extends SimpleNode {
  public CPPAST_CV_QUALIFIER_SEQ(int id) {
    super(id);
  }

  public CPPAST_CV_QUALIFIER_SEQ(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=396fd3c38beecccfefbc012e6dead0c0 (do not edit this line) */
