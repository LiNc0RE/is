% Die Schnittstelle umfasst
%   start_description   ;Beschreibung des Startzustands
%   start_node          ;Test, ob es sich um einen Startknoten handelt
%   goal_node           ;Test, ob es sich um einen Zielknoten handelt
%   state_member        ;Test, ob eine Zustandsbeschreibung in einer Liste
%                        von Zustandsbeschreibungen enthalten ist
%   expand              ;Berechnung der Kind-Zustandsbeschreibungen
%   eval-path           ;Bewertung eines Pfades
%
%
%   (Action, State, Value)


start_description([
  block(block1),
  block(block2),
  block(block3),
  block(block4),  %mit Block4
  on(table,block2),
  on(table,block3),
  on(block2,block1),
  on(table,block4), %mit Block4
  clear(block1),
  clear(block3),
  clear(block4), %mit Block4
  handempty
  ]).

goal_description([
  block(block1),
  block(block2),
  block(block3),
  block(block4), %mit Block4
  on(block4,block2), %mit Block4
  on(table,block3),
  on(table,block1),
  on(block1,block4), %mit Block4
%  on(block1,block2), %ohne Block4
  clear(block3),
  clear(block2),
  handempty
  ]).

state_description([
  block(block1),
  block(block2),
  block(block3),
  block(block4), %mit Block4
  on(block4,block2), %mit Block4
  on(table,block1),
  on(block1,block4), %mit Block4
  on(block2, block3),
%  on(block1,block2), %ohne Block4
  clear(block3),
  handempty
  ]).

is_on(on(_, _)).

% false_under(Block, CurrentState, IntersectionOfGoalAndCurrentState, Accu, Result)
% Returns list of blocks in wrong position, under block 'Block'.
false_under(table, _State, _Intersection, Result, Result).
false_under(Block, State, Intersection, Result, Result) :- member(on(Under, Block), State), member(on(Under, Block), Intersection).
false_under(Block, State, Intersection, Accu, Result) :- member(on(Under, Block), State), false_under(Under, State, Intersection, [Under | Accu], Result) .

% on(block4, block2)

%under(HigherBlock, State) :- findall(
all_false_under(_State,[],Result,Result).
all_false_under(State,[on(X,Y)|RestIntersection],Accu,_Result):- false_under(X,State,[on(X,Y)|RestIntersection],[],ResultFU),
                                                                     append(ResultFU,Accu,NewResult),all_false_under(State,RestIntersection,NewResult,NewResult).

start_node((start,_,_)).

goal_node((_,State,_)):- goal_description(Goal), subtract(State, Goal, []).
 % "Zielbedingungen einlesen"
 % "Zustand gegen Zielbedingungen testen".



% Aufgrund der Komplexit�t der Zustandsbeschreibungen kann state_member nicht auf
% das Standardpr�dikat member zur�ckgef�hrt werden.
%
state_member(_,[]):- !, fail.

state_member(State,[FirstState|_]):- subtract(State, FirstState, []), !.
%  "Test, ob State bereits durch FirstState beschrieben war. Tipp: Eine
%  L�sungsm�glichkeit besteht in der Verwendung einer Mengenoperation,
%  z.B. subtract" ,!.

%Es ist sichergestellt, dass die beiden ersten Klauseln nicht zutreffen.
state_member(State,[_|RestStates]):- state_member(State, RestStates).
  %"rekursiver Aufruf".
  
%%Kompliziertere Heuristik(funktioniert schlechter): Schaue dir an wie viele Bloecke unter "richtigen" Bloecken liegen und generiere daraus einen Wert
eval_path([(_,State,Value)|RestPath]):-
        goal_description(Goal),
        intersection(Goal, State, Intersection),
        include(is_on, Intersection, Is_Ons_Intersection),
        all_false_under(State,Is_Ons_Intersection,[],Result),
        length(Result,LengthResult),
        length(RestPath, LengthRestPath),
        Value is LengthResult+LengthRestPath.

%%Anzahl von noch falschen Zuständen
eval_path_([(_,State,Value)|RestPath]):-
        goal_description(Goal),
        include(is_on, Goal, Is_Ons_Goal),
        intersection(Goal, State, Intersection),
        include(is_on, Intersection, Is_Ons_Intersection),
        length(Is_Ons_Goal, LengthGoal),
        length(Is_Ons_Intersection, LengthIntersection),
        length(RestPath, LengthRestPath),
        Value is LengthGoal - LengthIntersection + LengthRestPath.
 % eval_state(State,"Rest des Literals bzw. der Klausel"
 % "Value berechnen".



action(pick_up(X),                           % action name
       [handempty, clear(X), on(table,X)],   % Conditions / C
       [handempty, clear(X), on(table,X)],   % DeleteList / D
       [holding(X)]).                        % AddList / A

action(pick_up(X),
       [handempty, clear(X), on(Y,X), block(Y)],
       [handempty, clear(X), on(Y,X)],
       [holding(X), clear(Y)]).

action(put_on_table(X),
       [holding(X)],
       [holding(X)],
       [handempty, clear(X), on(table,X)]).

action(put_on(Y,X),
       [holding(X), clear(Y)],
       [holding(X), clear(Y)],
       [handempty, clear(X), on(Y,X)]).


% Hilfskonstrukt, weil das PROLOG "subset" nicht die Unifikation von Listenelementen
% durchf�hrt, wenn Variablen enthalten sind. "member" unifiziert hingegen.
%
mysubset([],_).
mysubset([H|T],List):-
  member(H,List),
  mysubset(T,List).


expand_help(State,Name,NewState):-
  %writeln("Start expand help"),
  action(Name, C, D, A),        % "Action suchen"
  %write(" Name: "), writeln(Name),
  %write(" C: "), writeln(C), write(" D "), writeln(D),
  mysubset(C, State),                                  % "Conditions testen"
  %writeln(State),
  %write(" Name: "), writeln(Name),
  %write("C nach mysubset: "), writeln(C),
  %write("D nach subset: "), writeln(D),
  %write("A nach subset: "), writeln(A),
  subtract(State, D, ResultFromDelete),                % "Del-List umsetzen"
  %write(" Result from delete: "), writeln(ResultFromDelete),
  append(ResultFromDelete, A, NewState).
  %write("new state: "), writeln(NewState).       % "Add-List umsetzen".
  %list_to_set(ResultFromAppend, NewState).   % we don't need this, yeah?
expand((_,State,_),Result):-
  findall((Name,NewState,_),expand_help(State,Name,NewState),Result).


























