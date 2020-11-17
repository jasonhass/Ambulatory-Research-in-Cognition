//
// HMLog.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit

open class LogManager: NSObject {

    static public let sharedInstance = LogManager();
    
    public var logToFile:Bool = true;
    
   	public var fh: FileHandle?;
    public var fm = FileManager.default;
    public var logName:String = "Log.txt";
    
    static public func setLogToFile(log: Bool)
    {
        LogManager.sharedInstance.logToFile = log;
    }
    
    static public func logString(_ s:String)
    {
        LogManager.sharedInstance.logString(s);
    }
    public func getLog() -> Data?
    {
        let documentsDirecotry = fm.urls(for: .documentDirectory, in: .userDomainMask)[0];
        let filepath = documentsDirecotry.appendingPathComponent(logName)
        do {
            return try Data(contentsOf: filepath)
        } catch {
            print(error)
            return nil
        }

    }
    private func logString(_ s:String)
    {
        if logToFile == false
        {
            return;
        }
        
        let documentsDirecotry = fm.urls(for: .documentDirectory, in: .userDomainMask)[0];
        let filepath = documentsDirecotry.appendingPathComponent(logName);
        let line = "\(Date().localizedString(dateStyle: .short, timeStyle: .medium)): \(s)\n";
        
        do
        {
            if fm.fileExists(atPath: filepath.path) == false
            {
                try line.write(to: filepath, atomically: true, encoding: .utf8);
            }
            else
            {
                if fh == nil
                {
                    self.fh = try FileHandle(forWritingTo: filepath);
                }
                
                if let fileHandle = fh
                {
                    _ = fileHandle.seekToEndOfFile();
                    if let data = line.data(using: String.Encoding.utf8) {
                        fileHandle.write(data)
                    }
                }
            }
        }
        catch
        {
            print("error writing to log: \(error)");
        }
    }
    
}
//silence all log messages for production
public func HMLog(_ s: String, quiet:Bool = false, silent:Bool = !(Arc.environment?.isDebug ?? false))
{
	if !silent {
		if !quiet
		{
			print(s);
		}
		
		if LogManager.sharedInstance.logToFile
		{
			LogManager.logString(s);
		}
	}
}
