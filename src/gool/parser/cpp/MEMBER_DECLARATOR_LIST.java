/* Generated By:JJTree: Do not edit this line. MEMBER_DECLARATOR_LIST.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package gool.parser.cpp;

public
class MEMBER_DECLARATOR_LIST extends SimpleNode {
  public MEMBER_DECLARATOR_LIST(int id) {
    super(id);
  }

  public MEMBER_DECLARATOR_LIST(CPPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CPPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=7bbbca2080863cf5ca29686e50601ffd (do not edit this line) */
