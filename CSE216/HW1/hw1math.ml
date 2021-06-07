(*2.2*)
type expr =
  | Const of int
  | Var of string
  | Plus of arg
  | Mult of arg
  | Minus of arg
  | Div of arg
and
arg = {
  arg1: expr; arg2: expr
};;

(*2.3*)
let rec evaluate =
  function
  | Const x -> x
  | Var x -> 0
  | Plus {arg1 = e1; arg2 = e2} -> evaluate e1 + evaluate e2
  | Mult {arg1 = e1; arg2 = e2} -> evaluate e1 * evaluate e2
  | Minus {arg1 = e1; arg2 = e2} -> evaluate e1 - evaluate e2
  | Div {arg1 = e1; arg2 = e2} -> evaluate e1 / evaluate e2;;
