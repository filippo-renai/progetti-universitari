(* PROGETTO OCAML FILIPPO RENAI 530478 *)

type ide = string 

(*ambiente lista*)
type 't env = (ide * 't) list

exception WrongBindlist

let emptyenv(x) = [("", x)]

let rec applyenv(x, y) = 
match x with
| [(_, e)] -> e
| (i1, e1) :: x1 -> 
if y = i1 then e1 
else applyenv(x1, y)
| [] -> failwith("wrong env")    

let bind(r, l, e) = (l, e) :: r

let rec bindlist(r, il, el) = 
match (il, el) with
| ([], []) -> r
| (i::il1, e::el1) -> bindlist(bind(r, i, e), il1, el1)
| _ -> raise WrongBindlist;;

(*ambiente polimorfo              <--- non usato
type 't env = ide -> 't;;
let emptyenv (v : 't) = function x -> v;;
let applyenv (r : 't env) (i : ide) = r i;;
let bind (r : 't env) (i : ide) (v : 't) = function x -> if x = i then v else applyenv r x;; *)


(*sintassi linuaggio didattico*)
type exp = Eint of int
| Ebool of bool
| Den of ide
| Sum of exp * exp
| Diff of exp * exp
| Prod of exp * exp
| Eq of exp * exp
| Minus of exp
| Iszero of exp
| Or of exp * exp
| And of exp * exp
| Not of exp
| Ifthenelse of exp * exp * exp
| Let of ide * exp * exp(* Dichiarazione di ide: modifica ambiente*)
| Fun of ide (*parametro formale*) * exp(*body*)(* Astrazione di funzione*)
| Apply of exp * exp (*parametro attuale*)(* Applicazione di funzione*)
| Letrec of ide(*nome fun*)*ide(*par.formale*)*exp(*body fun*)*exp(*body let*)

(*Exp del progetto*)
| Etree of tree (* gli alberi sono anche espressioni *)
| ApplyOver of (ide list) * exp * exp (* applicazione di funzione ai nodi *)
| Select of ide * exp (* selezione di un nodo *)
and tree = Empty | Node of ide * exp * tree * tree;;


(*evT*)
type evT= Int of int
|Bool of bool
|Unbound
|RecFunVal of ide * ide * exp * evT env
|Funval of efun
|Tree of treeEnv
and treeEnv = EmptyTree | TreeNode of ide * evT * treeEnv * treeEnv
and efun = ide* exp * evT env;;




(*typechecking dinamico*)
let typecheck(x, y) = match x with
| "int" ->   
(match y with 
| Int(u) -> true
| _ -> false)
| "bool" -> 
(match y with
|Bool(b) -> true
|_->false)
|_->failwith("error");;


(*plus*)
let plus(x, y) = if typecheck("int", x) && typecheck("int", y) then  
(match (x, y) with 
|(Int(u), Int(w)) -> Int(u + w)
|_->failwith("error"))
else failwith ("error");;


(*diff*)
let diff(x,y)=if typecheck("int",x) && typecheck("int", y) then
(match (x, y) with 
|(Int(u), Int(w)) -> Int(u - w)
|_->failwith("error"))
else failwith ("type error");;


(*prod*)
let prod(x,y)=if typecheck("int",x) && typecheck("int", y) then
(match (x, y) with 
|(Int(u), Int(w)) -> Int(u * w)
|_->failwith("error"))
else failwith ("type error");;


(*iszero*)
let iszero(x)=if typecheck("int",x) then
(match x with
|Int(u)->if u=0 then Bool(true) else Bool(false)
|_->failwith("error"))
else failwith("type error");;


(*eq*)
let equ(x,y)=if typecheck("int",x) && typecheck("int", y) then
(match (x, y) with 
|(Int(u), Int(w)) -> if u=w then Bool(true) else Bool(false)
|_->failwith("error"))
else failwith ("type error");;


(*minus*)
let minus(x)=if typecheck("int",x) then
(match x with
|Int(u)->Int(-u)
|_->failwith("error"))
else failwith("type error");;



(*et*)
let et(x,y)=if typecheck("bool",x) && typecheck("bool", y) then
(match (x, y) with 
|(Bool(u), Bool(w)) -> Bool(u && w)
|_->failwith("error"))
else failwith ("type error");;



(*vel*)
let vel(x,y)=if typecheck("bool",x) && typecheck("bool", y) then
(match (x, y) with 
|(Bool(u), Bool(w)) -> Bool(u || w)
|_->failwith("error"))
else failwith ("type error");;



(*non*)
let non(x)=if typecheck("bool",x) then
(match x with 
|Bool(u) -> Bool(not(u))
|_->failwith("error"))
else failwith ("type error");;

(*funzione di supporto per il progetto*)
let rec find (x:ide) (lis:ide list) :bool = match lis with 
|[]->false
|y::ys->if x=y then true
else find x ys;;


(*interprete*)
let rec eval ((e: exp), (r: evT env)) : evT =
begin match e with
| Eint(n) -> Int(n)
| Ebool(b) -> Bool(b)
| Den(i) -> applyenv(r, i)
| Sum(a, b) ->  plus(eval(a, r), eval(b, r))
| Diff(a, b)  ->  diff(eval(a, r), eval(b, r))
| Prod(a,b)->prod(eval(a,r), eval(b,r))
| Iszero(a) -> iszero(eval(a, r))
| Eq(a, b) -> equ(eval(a, r),eval(b, r))
| Minus(a) ->  minus(eval(a, r))
| And(a, b) ->  et(eval(a, r), eval(b, r))
| Or(a, b) ->  vel(eval(a, r), eval(b, r))
| Not(a) -> non(eval(a, r))
| Ifthenelse(a, b, c) -> let g = eval(a, r) in
if typecheck("bool", g) then
(if g = Bool(true) then eval(b, r) else eval(c, r))
else failwith ("nonboolean guard")
| Let(i, e1, e2) -> eval(e2, bind (r, i, eval(e1, r))) 
| Fun(i,a) -> Funval(i,a,r)
| Letrec(f, i, fBody,letBody) -> 
let benv = 
bind(r, f, (RecFunVal(f, i, fBody, r)))
in eval(letBody, benv)
| Apply(Den f, eArg) -> 
(let fclosure= eval(Den f, r) in 
match fclosure with 
| Funval(arg, fbody, fDecEnv) -> 
eval(fbody, bind(fDecEnv, arg, eval(eArg, r))) 
| RecFunVal(f, arg, fbody, fDecEnv) ->
let aVal= eval(eArg, r) in 
let rEnv= bind(fDecEnv, f, fclosure) in
let aEnv= bind(rEnv, arg, aVal) in 
eval(fbody, aEnv)
| _ -> failwith("non functional value"))
| Apply(_,_) -> failwith("not function")

(*Eval delle funzioni di progetto*)
| Etree(a) ->
let rec valTree(albero:tree) :treeEnv =
match albero with
|Empty -> EmptyTree
|Node(id, ex, lt, rt) -> TreeNode(id, eval(ex, r), valTree(lt), valTree(rt))
in Tree( valTree(a) )

| ApplyOver(idl, exf, ext) ->
begin match eval(exf, r), eval(ext, r) with
|Funval(arg, fbody, fDecEnv),Tree(alb) ->
let rec applyFun (tr:treeEnv) :treeEnv  =
begin match tr with
|EmptyTree -> EmptyTree
|TreeNode(id, ex, lt, rt) -> 
if find id idl then 
TreeNode(id, eval(fbody, bind(fDecEnv, arg, ex)), applyFun(lt), applyFun(rt))
else TreeNode(id, ex, applyFun(lt), applyFun(rt))
end
in Tree( applyFun (alb) )
|(_, _) -> failwith("errore di tipo")
end

| Select(tag, a) -> 
begin
match eval(a,r) with
|Tree(al)-> 
let rec tmp (alb:treeEnv) :treeEnv= match alb with
|EmptyTree->EmptyTree
|TreeNode(id,ex,lt,rt) -> if id=tag then TreeNode(id,ex,lt,rt)
else 
let left = tmp (lt) in
let right = tmp (rt) in
match left, right with
|(EmptyTree, EmptyTree) ->EmptyTree
|(TreeNode(_),EmptyTree)->left
|(EmptyTree,TreeNode(_))->right
|(TreeNode(_),TreeNode(_))->failwith("i tag non sono univoci")
in Tree( tmp (al) )
|_->failwith("errore di tipo")
end
end;;

(*-------------------TEST-------------------------*)
let env0 = [];; (*dichiarazione ambiente vuoto*)

let albero_binario = (*dichiarazione dell albero*)
Etree(Node("a", Eint 1,      
Node("b", Eint 2, 
Node("d", Eint 4, Node("h", Eint 8, Empty, Empty), 
Node("i", Eint 9, Node("l", Eint 12, Empty, Empty), Empty)), 
Node("e", Eint 5, Empty, Empty)), 
Node("c", Eint 3, Node("f", Eint 6, Empty, Empty), 
Node("g", Eint 7, 
Node("j", Eint 10, Empty, 
Node("m", Eint 13, Empty, Empty)), 
Node("k", Eint 11, Empty, Empty)))));;

(*test ETree*)
eval(albero_binario,env0);;

(*test ApplyOver *)
eval(Let("g", Fun("x", Sum(Den "x", Eint 100)),  (*dichiarazione di funzione (somma il numero a 100)*)
Let("t", albero_binario,ApplyOver(["a";"b";"g";"k"], Den "g", Den "t"))),
env0);;

(*test Select (caso nodo non trovato)*)
eval(Select("non",albero_binario),env0);;

(*test Select (caso nodo trovato)*)
eval(Select("i",albero_binario),env0);;
