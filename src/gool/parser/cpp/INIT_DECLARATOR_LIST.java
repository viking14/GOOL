/* Generated By:JJTree: Do not edit this line. INIT_DECLARATOR_LIST.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class INIT_DECLARATOR_LIST extends SimpleNode {
  public INIT_DECLARATOR_LIST(int id) {
    super(id);
  }

  public INIT_DECLARATOR_LIST(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=8f6f085a38e52cae9da69c94bbc927d7 (do not edit this line) */