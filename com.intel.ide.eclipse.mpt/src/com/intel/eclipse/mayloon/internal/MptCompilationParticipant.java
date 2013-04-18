package com.intel.eclipse.mayloon.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import com.intel.ide.eclipse.mpt.ast.ASTParserAddNativeMethodDeclaration;
import com.intel.ide.eclipse.mpt.ast.ASTParserAddStubMethodDeclaration;
import com.intel.ide.eclipse.mpt.ast.ASTParserRemoveNativeMethodDeclaration;
import com.intel.ide.eclipse.mpt.ast.ASTParserRemoveStubMethodDeclaration;
import com.intel.ide.eclipse.mpt.ast.LocalNativeMethodDetector;

public class MptCompilationParticipant extends CompilationParticipant {

	private IJavaProject selectedProject;

	@Override
	public boolean isActive(IJavaProject project) {
		selectedProject = project;
		return true;
	}

	// give error message when save file
	@Override
	public void reconcile(ReconcileContext context) {
		return;
	}

	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}

	private void CreateStub() throws MalformedTreeException,
			BadLocationException, CoreException, IOException {

		// Update the user interface asynchronously
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					IProject project = selectedProject.getProject();

					if (project
							.isNatureEnabled("org.eclipse.jdt.core.javanature")) {

						IPackageFragment[] packages = JavaCore.create(project)
								.getPackageFragments();
						// parse(JavaCore.create(project));
						for (IPackageFragment mypackage : packages) {
							if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
								for (ICompilationUnit unit : mypackage
										.getCompilationUnits()) {

									
									// for local native method
									ASTParserAddNativeMethodDeclaration astParserAddNativeMethod = new ASTParserAddNativeMethodDeclaration();
									astParserAddNativeMethod.run(unit);									
									astParserAddNativeMethod.rewrite(astParserAddNativeMethod.getCompilationUnit(), astParserAddNativeMethod.getLocalStubMethodDetector().getNativeMethodBindingManagers());
									
									
									// for local method
//									ASTParserAddStubMethodDeclaration astParserAddStubMethod = new ASTParserAddStubMethodDeclaration();
//									astParserAddStubMethod.run(unit);									
//									astParserAddStubMethod.rewrite(astParserAddStubMethod.getCompilationUnit(), astParserAddStubMethod.getLocalStubMethodDetector().getStubMethodBindingManagers());

								}
							}
						}
					}
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedTreeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}

	// give error message when build project
	@SuppressWarnings("deprecation")
	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {

		try {
			CreateStub();
		} catch (MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// AST generate
	private CompilationUnit createAstFromFile(IFile file) {
		ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(cu);
		parser.setResolveBindings(true);
		CompilationUnit ast = (CompilationUnit) parser
				.createAST(new NullProgressMonitor());
		return ast;
	}

	// generate error message from AST
	private List<MptUnsupportedProblems> createProblems(final String fileName,
			final CompilationUnit ast) {
		final List<MptUnsupportedProblems> problems = new ArrayList<MptUnsupportedProblems>();

		ast.accept(new ASTVisitor() {
			@Override
			public boolean visit(MethodInvocation node) {
				MptUnsupportedProblems prom = new MptUnsupportedProblems(IStatus.ERROR, node
						.getName().toString(), fileName);
				int start = node.getStartPosition();
				prom.setSourceStart(start);
				prom.setSourceEnd(start + node.getLength());
				prom.setSourceLineNumber(ast.getLineNumber(start));
				problems.add(prom);
				return false;
			}
		});
		return problems;
	}

}
