/* Generated By:JJTree: Do not edit this line. BASE_SPECIFIER.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class BASE_SPECIFIER extends SimpleNode {
  public BASE_SPECIFIER(int id) {
    super(id);
  }

  public BASE_SPECIFIER(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d082c1a6f5f38ddda6c2b9b0b7bc64e7 (do not edit this line) */
