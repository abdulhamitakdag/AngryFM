[Setup]
AppName=AngryFM
AppVersion=1.0
AppPublisher=AngryFM Team
DefaultDirName={autopf}\AngryFM
DefaultGroupName=AngryFM
OutputDir=output
OutputBaseFilename=AngryFM_Setup
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "SportsManager\target\SportsManager-fat.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "UserManual.html"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\AngryFM";              Filename: "{sys}\cmd.exe"; Parameters: "/c ""{app}\AngryFM.bat"""; WorkingDir: "{app}"
Name: "{commondesktop}\AngryFM";      Filename: "{sys}\cmd.exe"; Parameters: "/c ""{app}\AngryFM.bat"""; WorkingDir: "{app}"
Name: "{group}\AngryFM User Manual";  Filename: "{app}\UserManual.html"

[Run]
Filename: "{sys}\cmd.exe"; Parameters: "/c ""{app}\AngryFM.bat"""; Description: "Launch AngryFM";     Flags: nowait postinstall skipifsilent
Filename: "{app}\UserManual.html";                                  Description: "View User Manual";  Flags: nowait postinstall skipifsilent shellexec unchecked

[UninstallDelete]
Type: files; Name: "{app}\AngryFM.bat"

[Code]
procedure CreateRunScript;
var
  ScriptPath: string;
  Lines: TStringList;
begin
  ScriptPath := ExpandConstant('{app}\AngryFM.bat');
  Lines := TStringList.Create;
  try
    Lines.Add('@echo off');
    Lines.Add('java -jar "%~dp0SportsManager-fat.jar"');
    Lines.Add('if errorlevel 1 pause');
    Lines.SaveToFile(ScriptPath);
  finally
    Lines.Free;
  end;
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then
    CreateRunScript;
end;
