/* Generated By:JJTree: Do not edit this line. ENUM_SPECIFIER.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class ENUM_SPECIFIER extends SimpleNode {
  public ENUM_SPECIFIER(int id) {
    super(id);
  }

  public ENUM_SPECIFIER(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=8377e82f036a881e67d32117e429c5bb (do not edit this line) */
